package com.quakearts.identity.facelets.listener;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.quakearts.identity.hibernate.UserLog;
import com.quakearts.identity.hibernate.UserRole;
import com.quakearts.webapp.facelets.base.BaseBean;
import com.quakearts.webapp.orm.DataStore;
import com.quakearts.webapp.orm.DataStoreFactory;
import com.quakearts.webapp.orm.query.helper.ParameterMapBuilder;

@ManagedBean(name="userManagement")
@ViewScoped
public class UserManagementBean extends BaseBean {

	/**
	 * 
	 */
	private String userroles;
	private String[] roles;
	private static final long serialVersionUID = -7063591289223968569L;
	private UserLog user = new UserLog();
	
	public UserLog getUser() {
		if(user == null && hasParameter("id")){
			try {
				user = DataStoreFactory.getInstance().getDataStore().get(UserLog.class, getParameterLong("id"));				
			} catch (NumberFormatException e) {
				addError("Invalid id", "The id provided is invalid", FacesContext.getCurrentInstance());
				return null;
			}
		
			if(user == null)
				addError("User not found", "User cannot be found", FacesContext.getCurrentInstance());
		}
		
		return user;
	}
	
	public void setUser(UserLog user) {
		this.user = user;
	}

	public String getUserroles() {
		return userroles;
	}

	public void setUserroles(String userroles) {
		this.userroles = userroles;
	}

	public String[] getRoles() {
		return roles;
	}

	public void setRoles(String[] roles) {
		this.roles = roles;
	}

	public void addUser(ActionEvent event) {
		if(user == null)
			return;
		
		FacesContext ctx = FacesContext.getCurrentInstance();
		DataStore dataStore = DataStoreFactory.getInstance().getDataStore();
		List<UserLog> ousers = dataStore.list(UserLog.class,ParameterMapBuilder.createParameters()
				.add("username", user.getUsername()).build());

		if(!ousers.isEmpty()){
			addError("Duplication error",
					"Username already exists on the system. " + user.getUsername(),
					FacesContext.getCurrentInstance());
			return;
		}
		
		dataStore.save(user);

		if(userroles!=null && !userroles.trim().isEmpty()){
			String[] customroles = userroles.split(";");
			for (String role : customroles) {
				UserRole userrole = new UserRole();
				userrole.setRoleName(role.trim());
				userrole.setUserLog(user);
				userrole.setValid(true);
				dataStore.save(userrole);
			}
		}
		
		if(roles!=null)
			for(String role : roles){
				UserRole userrole = new UserRole();
				userrole.setRoleName(role.trim());
				userrole.setUserLog(user);
				userrole.setValid(true);
				dataStore.save(userrole);
			}
		
		addMessage("Success", "User added successfully", ctx);
		user = new UserLog();
	}

	
	public void deleteUser(ActionEvent event) {
		 if(user.getUsername().equals("administrator")){
			 addError("Invalid operation", "You cannot delete this user", FacesContext.getCurrentInstance());
			 return;
		 }
			 
		
		if(user !=null && user.getId()>0){
			DataStoreFactory.getInstance().getDataStore().delete(user);
			setOutcome("success");
			addMessage("User deleted", "User has been successfully deleted.", FacesContext.getCurrentInstance());
		} else {
			setOutcome("error");
			addMessage("Invalid user", "Object returned was not a valid user.", FacesContext.getCurrentInstance());
		}
	}
	
	public void updateUser(ActionEvent event) {
		if(user !=null && user.getId()>0){
			DataStoreFactory.getInstance().getDataStore().update(user);
			setOutcome("success");
			addMessage("User modified", "User has been successfully updated.", FacesContext.getCurrentInstance());
		} else {
			setOutcome("error");
			addMessage("Invalid user", "Object returned was not a valid user.", FacesContext.getCurrentInstance());
		}
	}
}
