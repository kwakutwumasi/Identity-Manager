package com.quakearts.identity.auth;

import java.io.IOException;
import java.security.Principal;
import java.security.acl.Group;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import com.quakearts.webapp.orm.query.helper.ParameterMapBuilder;
import com.quakearts.webapp.security.auth.DirectoryRoles;
import com.quakearts.webapp.security.auth.OtherPrincipal;
import com.quakearts.webapp.security.auth.UserPrincipal;
import com.quakearts.webapp.security.util.HashPassword;

public class IdentityLoginModule implements LoginModule {
	private static final String IDENTITY_USER = "IdentityUser";
	private Subject subject;
	private CallbackHandler callbackHandler;
	@SuppressWarnings("rawtypes")
	private Map sharedState;
	private UserLog user;
	private boolean loginOk;
	private String roleGroupName;
	private String[] defaultroles;
	
	@SuppressWarnings("rawtypes")
	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState,
			Map options) {
		this.callbackHandler = callbackHandler;
		this.sharedState = sharedState;
		this.subject = subject;
		
        String defaultroles_str = (String) options.get("default.roles");
        roleGroupName =  (String) options.get("roles.group.name");
		if(defaultroles_str != null){
        	defaultroles = defaultroles_str.split(";");
        	for(int i=0;i<defaultroles.length;i++)
        		defaultroles[i] = defaultroles[i].trim();
        }
		
		if (roleGroupName == null)
			roleGroupName = new String("Roles");

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
			if(localTran=(tran.getStatus() == Status.STATUS_NO_TRANSACTION)){
				tran.begin();
			}
		} catch (SystemException e) {
			throw new LoginException("Cannot start transaction");
		} catch (NotSupportedException e) {
			throw new LoginException("Cannot start transaction");
		}
			
		try{
			List<UserLog> users = DataStoreFactory.getInstance().getDataStore().list(UserLog.class,
					ParameterMapBuilder.createParameters().add("username", username)
					.add("password", hashPassword).build());
			
			if((loginOk = !users.isEmpty())){
				user = users.get(0);
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

	@SuppressWarnings("rawtypes")
	@Override
	public boolean commit() throws LoginException {
		Set<Principal> principalset = subject.getPrincipals();
		Group rolesgrp=null;
		for (Iterator i = principalset.iterator(); i.hasNext();) {
			Object obj = i.next();
			if (obj instanceof Group && ((Group) obj).getName().equals(roleGroupName)) {
				rolesgrp = (Group) obj;
				break;
			}
		}
		
		if(rolesgrp==null){
			rolesgrp = new DirectoryRoles(roleGroupName);
			principalset.add(rolesgrp);
		}

		rolesgrp.addMember(new OtherPrincipal(IDENTITY_USER));
		rolesgrp.addMember(new UserPrincipal(user.getUsername()));
		
		for(UserRole role:user.getUserRoles()){
			OtherPrincipal principal = new OtherPrincipal(role.getRoleName(),
					role.getRoleName());
			rolesgrp.addMember(principal);
		}
		
		if(defaultroles != null)
			for(String defaultRole:defaultroles){
				OtherPrincipal principal = new OtherPrincipal(defaultRole,
						defaultRole);
				rolesgrp.addMember(principal);			
			}
		
		Enumeration<? extends Principal> members = rolesgrp.members();
		while (members.hasMoreElements()) {
			Principal type = members.nextElement();
			principalset.add(type);				
		}
		
		user = null;
		return loginOk;
	}

	@Override
	public boolean abort() throws LoginException {
		loginOk=false;
		user=null;
		return true;
	}

	@Override
	public boolean logout() throws LoginException {
		loginOk=false;
		user=null;
		return true;
	}

}
