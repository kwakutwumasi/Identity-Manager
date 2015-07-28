package com.quakearts.identity.facelets.listener;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import com.quakearts.identity.hibernate.UserLog;
import com.quakearts.identity.hibernate.UserRole;
import com.quakearts.webapp.facelets.tag.BaseBean;
import com.quakearts.webapp.hibernate.HibernateHelper;

@ManagedBean(name = "roleManagement")
@ViewScoped
public class RoleManagementBean extends BaseBean {

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
			for(String role:roles){
				UserRole userrole = new UserRole();
				userrole.setRoleName(role);
				userrole.setUserLog(user);
				HibernateHelper.getCurrentSession().save(userrole);
				addMessage("Role modified", "The role " + userRole.getRoleName() + " was succesfully added", ctx);
			}
		} else {
			addError("User not found", "The user does not exist", ctx);
		}
	}

	public void editRole(ActionEvent event) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		if (userRole != null && userRole.getRid() > 0) {
			HibernateHelper.getCurrentSession().update(userRole);
			setOutcome("success");
			addMessage("Role modified", "The role was succesfully edited", ctx);
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
			HibernateHelper.getCurrentSession().delete(userRole);
			setOutcome("success");
			addMessage("Role deleted", "The roles "+userRole.getRoleName()+" was succesfully deleted", ctx);
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
