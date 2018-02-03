package com.quakearts.identity;

import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.faces.event.AbortProcessingException;

import com.quakearts.identity.model.UserLog;
import com.quakearts.identity.model.UserRole;
import com.quakearts.identity.util.IdentityConfig;
import com.quakearts.webapp.orm.DataStore;
import com.quakearts.webapp.orm.DataStoreFactory;
import com.quakearts.webapp.orm.query.helper.ParameterMapBuilder;
import com.quakearts.webapp.security.util.HashPassword;

public class IdentityManagerBootstrap {
	public void boostrapIdentityModule() {
		DataStore dataStore = DataStoreFactory.getInstance().getDataStore();
		List<UserLog> users =  dataStore
				.list(UserLog.class, ParameterMapBuilder.createParameters().add("username", "administrator").build());

		if(users.isEmpty()){
			
			Properties props = IdentityConfig.getIdentityProperties();
			if(props==null)
				throw new AbortProcessingException("Could not load properties");
			
			
			String newPassword = generateRandomPassword();
			System.out.println("********************************************************************"
						+ "\r\nGenerating new password for Administrator:\r\n"+newPassword
						+"\r\nPlease ensure that this password is changed to avoid security issues."
						+ "\r\n*********************************************************************");
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
	
	private String generateRandomPassword() {
		Random random = new Random();
		StringBuilder builder = new StringBuilder();
		for(int i=0;i<10;i++) {
			builder.append(characterSet[Math.abs(random.nextInt() % characterSet.length)]);
		}
		return builder.toString();
	}
}
