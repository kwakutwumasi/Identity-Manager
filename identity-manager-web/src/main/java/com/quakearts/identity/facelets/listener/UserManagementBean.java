package com.quakearts.identity.facelets.listener;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.quakearts.identity.model.UserLog;
import com.quakearts.identity.model.UserRole;
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
	private UserLog user;
	
	public UserLog getUser() {
		if(user == null) {
			if(hasParameter("id")){
				try {
					user = DataStoreFactory.getInstance().getDataStore().get(UserLog.class, getParameterLong("id"));
				} catch (NumberFormatException e) {
					addError("Invalid id", "The id provided is invalid", FacesContext.getCurrentInstance());
					return null;
				}
			
				if(user == null)
					addError("User not found", "User cannot be found", FacesContext.getCurrentInstance());
			} else {
				user = new UserLog();
			}
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
		if(user == null) {
			addError("Invalid Entry",
					"User cannot be empty.",
					FacesContext.getCurrentInstance());
			return;
		}
		
		if(user.getUsername() == null || user.getUsername().trim().isEmpty()) {
			addError("Invalid Entry",
					"Username cannot be empty.",
					FacesContext.getCurrentInstance());
			return;
		}

		if(user.getPassword() == null || user.getPassword().trim().isEmpty()) {
			addError("Invalid Entry",
					"Password cannot be empty.",
					FacesContext.getCurrentInstance());
			return;
		}

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
		dataStore.flushBuffers();

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
			DataStore dataStore = DataStoreFactory.getInstance().getDataStore();
			for(UserRole role:user.getUserRoles()) {
				dataStore.delete(role);
			}
			dataStore.delete(user);
			addMessage("User deleted", "User has been successfully deleted.", FacesContext.getCurrentInstance());
		} else {
			addMessage("Invalid user", "Object returned was not a valid user.", FacesContext.getCurrentInstance());
		}
	}
	
	public void updateUser(ActionEvent event) {
		if(user !=null && user.getId()>0){
			DataStoreFactory.getInstance().getDataStore().update(user);
			addMessage("User modified", "User has been successfully updated.", FacesContext.getCurrentInstance());
		} else {
			addMessage("Invalid user", "Object returned was not a valid user.", FacesContext.getCurrentInstance());
		}
	}
}
