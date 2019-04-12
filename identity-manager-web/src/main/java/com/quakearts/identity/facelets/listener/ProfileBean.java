package com.quakearts.identity.facelets.listener;

import java.util.Optional;

import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.quakearts.identity.facelets.validator.PasswordComplexityValidator;
import com.quakearts.identity.model.UserLog;
import com.quakearts.webapp.facelets.base.BaseBean;
import com.quakearts.webapp.orm.DataStore;
import com.quakearts.webapp.orm.DataStoreFactory;

@Named("profile")
@ViewScoped
public class ProfileBean extends BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8563015775030904683L;
	private UserLog user;

	public UserLog getUser() {
		if(user==null){
			FacesContext ctx= FacesContext.getCurrentInstance();
			DataStore dataStore = DataStoreFactory.getInstance().getDataStore();
			
			String username = ctx.getExternalContext().getRemoteUser();
			findUser(ctx, dataStore, username);
		}
		return user;
	}

	private void findUser(FacesContext ctx, DataStore dataStore, String username) {
		if(username !=null && username.trim().length()>0){
			Optional<UserLog> optionalUser = dataStore.find(UserLog.class).filterBy("username")
					.withAValueEqualTo(username).thenGetFirst();
			checkUserExistence(ctx, username, optionalUser);
		} else {
			addError("No profile loaded", "You must be logged in to use this page", ctx);
		}
	}

	private void checkUserExistence(FacesContext ctx, String username, Optional<UserLog> optionalUser) {
		if(optionalUser.isPresent()){
			checkUserValidity(ctx, optionalUser.get());
		} else {
			addError("No profile loaded", "Username "+username+" does not exist in this database.", ctx);
		}
	}

	private void checkUserValidity(FacesContext ctx, UserLog foundUser) {
		if(!foundUser.isValid()){
			addError("Profile Inactive", "Your profile is not active.", ctx);
		} else {
			user = foundUser;
		}
	}

	@Inject
	private PasswordComplexityValidator passwordComplexityValidator;
	
	public Validator getPasswordComplexityValidator() {		
		return passwordComplexityValidator;
	}
}
