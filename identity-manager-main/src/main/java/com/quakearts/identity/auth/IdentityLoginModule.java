package com.quakearts.identity.auth;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import com.quakearts.identity.model.UserLog;
import com.quakearts.identity.model.UserRole;
import com.quakearts.identity.util.IdentityConfig;
import com.quakearts.webapp.orm.DataStoreFactory;
import com.quakearts.webapp.security.auth.OtherPrincipal;
import com.quakearts.webapp.security.auth.UserPrincipal;
import com.quakearts.webapp.security.auth.util.AttemptChecker;
import com.quakearts.webapp.security.util.HashPassword;

public class IdentityLoginModule implements LoginModule {
	private static final String IDENTITY_MANAGER = "Identity-Manager";
	private static final String IDENTITY_USER = "IdentityUser";
	private Subject subject;
	private CallbackHandler callbackHandler;
	@SuppressWarnings("rawtypes")
	private Map sharedState;
	private UserLog user;
	private boolean loginOk;
	private String[] defaultroles;
	
	static {
		int maxAttempts = Integer.parseInt(IdentityConfig.getIdentityProperties().getProperty("max.login.attempts", "4"));
		int lockoutTime = Integer.parseInt(IdentityConfig.getIdentityProperties().getProperty("lockout.time", "360000"));
		AttemptChecker.createChecker(IDENTITY_MANAGER, maxAttempts, lockoutTime);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState,
			Map options) {
		this.callbackHandler = callbackHandler;
		this.sharedState = sharedState;
		this.subject = subject;
		
        String defaultrolesString = (String) options.get("default.roles");
		if(defaultrolesString != null){
        	defaultroles = defaultrolesString.split(";");
        	for(int i=0;i<defaultroles.length;i++)
        		defaultroles[i] = defaultroles[i].trim();
        }
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean login() throws LoginException {
		String username = null;
		char[] password = null;
		Callback[] callbacks = new Callback[2];
		NameCallback name = new NameCallback("Enter your username");
		PasswordCallback pass = new PasswordCallback(
				"Enter your password:", false);
		callbacks[0] = name;
		callbacks[1] = pass;

		try {
			callbackHandler.handle(callbacks);
		} catch (UnsupportedCallbackException e) {
			throw new LoginException(
					"Callback is not supported");
		} catch (IOException e) {
			throw new LoginException(
					"Callback could not be completed because of an IO error");
		}

		if (sharedState != null) {
			UserPrincipal shareduser = new UserPrincipal(username);
			sharedState.put("javax.security.auth.login.name",
					shareduser);
			char[] sharedpass = password;
			sharedState.put("javax.security.auth.login.password",
					sharedpass);
		}
		
		username = name.getName() == null ? name.getDefaultName()
				: name.getName();
		password = pass.getPassword();
		
		AttemptChecker attemptChecker = AttemptChecker.getChecker(IDENTITY_MANAGER);
		if(attemptChecker.isLocked(username)){
			throw new LoginException("User profile has been locked");
		}

		String hashPassword = new HashPassword(new String(password), 
				IdentityConfig.getAlgorithm(), 
				IdentityConfig.getIterations(),
					IdentityConfig.getSalt()).toString();
		UserTransaction tran;
		try {
			tran = (UserTransaction) getInitialContext().lookup("java:comp/UserTransaction");
		} catch (NamingException e) {
			throw new LoginException("Cannot start transaction");
		}
		
		boolean localTran = false;
		try {
			localTran=(tran.getStatus() == Status.STATUS_NO_TRANSACTION);
			if(localTran){
				tran.begin();
			}
		} catch (SystemException | NotSupportedException e) {
			throw new LoginException("Cannot start transaction");
		}
			
		try {
			attemptChecker.incrementAttempts(username);
			Optional<UserLog> users = DataStoreFactory.getInstance().getDataStore().find(UserLog.class)
					.filterBy("username").withAValueEqualTo(username)
					.filterBy("password").withAValueEqualTo(hashPassword)
					.thenGetFirst();
			
			loginOk = users.isPresent();
			if(loginOk){
				user = users.get();
				attemptChecker.reset(username);
			}
		} finally {
			if(localTran)
				try {
					tran.commit();
				} catch (SecurityException | IllegalStateException | RollbackException | HeuristicMixedException
						| HeuristicRollbackException | SystemException e) {
					//ignore
				}
		}
		
		return loginOk;
	}

	private InitialContext context;
	private InitialContext getInitialContext() throws NamingException {
		if(context == null)
			context = new InitialContext();

		return context;
	}

	@Override
	public boolean commit() throws LoginException {
		Set<Principal> principalset = subject.getPrincipals();

		principalset.add(new OtherPrincipal(IDENTITY_USER));
		principalset.add(new UserPrincipal(user.getUsername()));
		
		for(UserRole role:user.getUserRoles()){
			OtherPrincipal principal = new OtherPrincipal(role.getRoleName(),
					role.getRoleName());
			principalset.add(principal);
		}
		
		if(defaultroles != null)
			for(String defaultRole:defaultroles){
				OtherPrincipal principal = new OtherPrincipal(defaultRole,
						defaultRole);
				principalset.add(principal);			
			}
				
		user = null;
		return loginOk;
	}

	@Override
	public boolean abort() throws LoginException {
		return logout();
	}

	@Override
	public boolean logout() throws LoginException {
		loginOk=false;
		user=null;
		return true;
	}

}
