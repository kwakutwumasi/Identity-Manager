package com.quakearts.identity.system;

import java.util.List;
import java.util.Properties;

import javax.faces.application.Application;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PostConstructApplicationEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;
import com.quakearts.identity.facelets.util.IdentityConfig;
import com.quakearts.identity.hibernate.UserLog;
import com.quakearts.identity.hibernate.UserRole;
import com.quakearts.webapp.orm.DataStore;
import com.quakearts.webapp.orm.DataStoreFactory;
import com.quakearts.webapp.orm.query.helper.ParameterMapBuilder;
import com.quakearts.webapp.security.util.HashPassword;

public class AdministratorCreateEvent implements SystemEventListener {

	@Override
	public void processEvent(SystemEvent event) throws AbortProcessingException {
		if (event instanceof PostConstructApplicationEvent) {			
			DataStore dataStore = DataStoreFactory.getInstance().getDataStore();
			List<UserLog> users = 
					dataStore.list(UserLog.class, ParameterMapBuilder.createParameters().add("username", "administrator").build());

			if(users.isEmpty()){
				
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
				
				UserLog user= new UserLog("administrator", password.toString(), true);
				dataStore.save(user);
				
				UserRole role = new UserRole();
				role.setRoleName("IdentityAdmin");
				role.setUserLog(user);
				role.setValid(true);
				
				dataStore.save(role);
			}
		}
	}

	@Override
	public boolean isListenerForSource(Object source) {
	      return (source instanceof Application);
	}

}
