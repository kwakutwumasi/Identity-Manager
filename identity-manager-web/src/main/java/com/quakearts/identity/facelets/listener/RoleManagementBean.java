package com.quakearts.identity.facelets.listener;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import com.quakearts.identity.model.UserLog;
import com.quakearts.identity.model.UserRole;
import com.quakearts.webapp.facelets.base.BaseBean;
import com.quakearts.webapp.orm.DataStoreFactory;

@Named("roleManagement")
@ViewScoped
public class RoleManagementBean extends BaseBean {

	private static final String WAS_SUCCESFULLY_ADDED = " was succesfully added";
	private static final String THE_ROLE = "The role ";
	private static final String ROLE_MODIFIED = "Role modified";
	/**
	 * 
	 */
	private static final long serialVersionUID = -4606311533405544332L;
	private String[] roles;
	private String customroles;
	private UserLog user;
	private UserRole userRole;

	public UserRole getUserRole() {
		return userRole;
	}

	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}

	public UserLog getUser() {
		return user;
	}

	public void setUser(UserLog user) {
		this.user = user;
	}

	public String[] getRoles() {
		return roles;
	}

	public void setRoles(String[] roles) {
		this.roles = roles;
	}

	public String getCustomroles() {
		return customroles;
	}
	
	public void setCustomroles(String customroles) {
		this.customroles = customroles;
	}
	
	public void addRole(ActionEvent event) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		
		if (roles == null && customroles==null) {
			addError("Error", "No new role was specified", ctx);
			return;
		}

		if (user != null && user.getId() > 0) {
			if(roles!=null){
				for(String role:roles){
					UserRole newUserRole = new UserRole();
					newUserRole.setRoleName(role);
					newUserRole.setUserLog(user);
					user.getRolesList().add(newUserRole);
					DataStoreFactory.getInstance().getDataStore().save(newUserRole);
					addMessage(ROLE_MODIFIED, THE_ROLE + newUserRole.getRoleName() + WAS_SUCCESFULLY_ADDED, ctx);
				}
			}
			if(customroles!=null){
				for(String role:customroles.split(";")){
					UserRole newUserRole = new UserRole();
					newUserRole.setRoleName(role);
					newUserRole.setUserLog(user);
					user.getRolesList().add(newUserRole);
					DataStoreFactory.getInstance().getDataStore().save(newUserRole);
					addMessage(ROLE_MODIFIED, THE_ROLE + newUserRole.getRoleName() + WAS_SUCCESFULLY_ADDED, ctx);
				}
			}
		} else {
			addError("User not found", "The user does not exist", ctx);
		}
	}

	public void editRole(ActionEvent event) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		if (userRole != null && userRole.getRid() > 0) {
			DataStoreFactory.getInstance().getDataStore().update(userRole);
			setOutcome("success");
			addMessage(ROLE_MODIFIED, "The role was succesfully edited", ctx);
		} else {
			setOutcome("error");
			addError(
					"Role not found", userRole != null
					? "The role with named " + userRole.getRoleName() + " does not exist" 
					: "Role is null",
					ctx);
		}
	}

	public void deleteRole(ActionEvent event) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		if (userRole != null && userRole.getRid() > 0) {
			UserLog userLog = userRole.getUserLog();
			if(userLog.getRolesList()!=null)
				userLog.getRolesList().remove(userRole);
			
			DataStoreFactory.getInstance().getDataStore().delete(userRole);
			setOutcome("success");
			addMessage("Role deleted", THE_ROLE+userRole.getRoleName()+" was succesfully deleted", ctx);
			userRole = null;
		} else {
			setOutcome("error");
			addError(
					"Role not found", userRole != null
					? "The roles with named " + userRole.getRoleName() + " does not exist" 
					: "Role is null",
					ctx);
		}
	}	
	
}
