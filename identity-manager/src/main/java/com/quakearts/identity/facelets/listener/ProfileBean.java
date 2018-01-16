package com.quakearts.identity.facelets.listener;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import com.quakearts.identity.hibernate.UserLog;
import com.quakearts.webapp.facelets.base.BaseBean;
import com.quakearts.webapp.orm.DataStore;
import com.quakearts.webapp.orm.DataStoreFactory;
import com.quakearts.webapp.orm.query.helper.ParameterMapBuilder;

@ManagedBean(name="profile")
@SessionScoped
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
			
			if(username !=null && username.trim().length()>0){
				List<UserLog> users = dataStore.list(UserLog.class, ParameterMapBuilder.createParameters()
						.add("username", username).build());
				user = users.isEmpty()?null:users.get(0);
				if(user !=null){
					if(!user.isValid()){
						addError("Profile Inactive", "Your profile is not active.", ctx);
						return null;
					}
				} else {
					addError("No profile loaded", "Username "+username+" does not exist in this database.", ctx);
				}
			} else {
				addError("No profile loaded", "You must be logged in to use this page", ctx);
			}
		}
		return user;
	}

}
