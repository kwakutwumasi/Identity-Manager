package com.quakearts.identity;

import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.quakearts.identity.exception.IdentityManagerRuntimeException;
import com.quakearts.identity.model.UserLog;
import com.quakearts.identity.model.UserRole;
import com.quakearts.identity.util.IdentityConfig;
import com.quakearts.webapp.orm.DataStore;
import com.quakearts.webapp.orm.DataStoreFactory;
import com.quakearts.webapp.orm.query.criteria.CriteriaMapBuilder;
import com.quakearts.webapp.security.util.HashPassword;

public class IdentityManagerBootstrap {
	private static final Logger log = LoggerFactory.getLogger(IdentityManagerBootstrap.class);
	
	public void boostrapIdentityModule() {
		DataStore dataStore = DataStoreFactory.getInstance().getDataStore();
		List<UserLog> users =  dataStore
				.find(UserLog.class).using(CriteriaMapBuilder.createCriteria()
						.property("username").mustBeEqualTo("administrator")
						.finish())
				.thenList();

		if(users.isEmpty()){
			
			Properties props = IdentityConfig.getIdentityProperties();
			if(props==null)
				throw new IdentityManagerRuntimeException("Could not load identity manager properties: identity.properties");
			
			
			String newPassword = generateRandomPassword();
			log.info("********************************************************************"
						+ "\r\nGenerating new password for Administrator:\r\n{}"
						+ "\r\nPlease ensure that this password is changed to avoid security issues."
						+ "\r\n*********************************************************************", newPassword);
			HashPassword password = new HashPassword(newPassword, IdentityConfig.getAlgorithm(), 
					IdentityConfig.getIterations(), 
					IdentityConfig.getSalt());
			
			UserLog user= new UserLog("administrator", password.toString(), true);
			dataStore.save(user);
			
			UserRole role = new UserRole();
			role.setRoleName("IdentityAdmin");
			role.setUserLog(user);
			role.setValid(true);
			
			dataStore.save(role);
		}
	}

	private char[] characterSet = ("abcdefghujklmnopqrstuvwxyz"
			+ "ABCDEFGHUJKLMNOPQRSTUVWXYZ"
			+ "0123456789"
			+ "!@$^*_").toCharArray();
	
	private Random random = new Random();
	
	private String generateRandomPassword() {
		StringBuilder builder = new StringBuilder();
		for(int i=0;i<10;i++) {
			builder.append(characterSet[Math.abs(random.nextInt() % characterSet.length)]);
		}
		return builder.toString();
	}
}
