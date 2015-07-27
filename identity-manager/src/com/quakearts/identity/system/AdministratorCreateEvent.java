package com.quakearts.identity.system;

import java.util.Properties;

import javax.faces.application.Application;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PostConstructApplicationEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;
import javax.naming.NamingException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import com.quakearts.identity.facelets.util.IdentityConfig;
import com.quakearts.identity.hibernate.UserLog;
import com.quakearts.identity.hibernate.UserRole;
import com.quakearts.webapp.facelets.util.UtilityMethods;
import com.quakearts.webapp.hibernate.HibernateHelper;
import com.quakearts.webapp.security.util.HashPassword;

public class AdministratorCreateEvent implements SystemEventListener {

	@Override
	public void processEvent(SystemEvent event) throws AbortProcessingException {
		if (event instanceof PostConstructApplicationEvent) {
			UserTransaction tran;
			try {
				tran = (UserTransaction) UtilityMethods.getInitialContext().lookup("UserTransaction");
			} catch (NamingException e) {
				throw new AbortProcessingException("Cannot start transaction");
			}
			
			boolean localTran = false;
			try {
				if(localTran=(tran.getStatus() == Status.STATUS_NO_TRANSACTION)){
					tran.begin();
				}
			} catch (SystemException e) {
				throw new AbortProcessingException("Cannot start transaction");
			} catch (NotSupportedException e) {
				throw new AbortProcessingException("Cannot start transaction");
			}
			
			try {
				Session session = HibernateHelper.getCurrentSession();
				UserLog user = (UserLog) session.createCriteria(UserLog.class).add(Restrictions.eq("username", "administrator")).uniqueResult();
				if(user==null){
					
					Properties props = IdentityConfig.getIdentityProperties();
					if(props==null)
						throw new AbortProcessingException("Could not load properties");
					
					String alg = props.getProperty("hash.alg");
					if(alg==null)
						alg=IdentityConfig.DEFAULT_ALG;
	
					String salt = props.getProperty("hash.salt");
					if(salt==null)
						salt=IdentityConfig.DEFAULT_SALT;
						
					int iterations;
					try {
						iterations = Integer.parseInt(props.getProperty("hash.iterations"));
					} catch (Exception e) {
						iterations = IdentityConfig.DEFAULT_ITERATIONS;
					}
					
					HashPassword password = new HashPassword("P@ssw0rd1", alg, iterations, salt);
					
					user= new UserLog("administrator", password.toString(), true);
					session.save(user);
					
					UserRole role = new UserRole();
					role.setRoleName("IdentityAdmin");
					role.setUserLog(user);
					role.setValid(true);
					
					session.save(role);
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
		}
	}

	@Override
	public boolean isListenerForSource(Object source) {
	      return (source instanceof Application);
	}

}
