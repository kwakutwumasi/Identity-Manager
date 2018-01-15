package com.quakearts.identity.facelets.listener;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.hibernate.NonUniqueObjectException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.quakearts.identity.hibernate.UserLog;
import com.quakearts.identity.hibernate.UserRole;
import com.quakearts.webapp.facelets.tag.BaseBean;
import com.quakearts.webapp.hibernate.HibernateHelper;

@ManagedBean(name="userManagement")
@ViewScoped
public class UserManagementBean extends BaseBean {

	/**
	 * 
	 */
	private String userroles;
	private String[] roles;
	private static final long serialVersionUID = -7063591289223968569L;
	private long id;
	private UserLog user = new UserLog();
	private boolean reload;
	
	public UserLog getUser() {
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

	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		if(reload = !(this.id==id))
			this.id = id;
	}
	
	public void loadUser(ActionEvent e) {
		if(reload){
			Session session = HibernateHelper.getCurrentSession();
			user = (UserLog) session.get(UserLog.class, id);
		
			if(user==null)
				addError("User not found", "User cannot be found", FacesContext.getCurrentInstance());
		}
	}
	
	public void addUser(ActionEvent event) {
		if(user==null)
			return;
		
		FacesContext ctx = FacesContext.getCurrentInstance();
		
		Session session = HibernateHelper.getCurrentSession();
		
		try {
			UserLog ouser = (UserLog) session.createCriteria(UserLog.class)
					.add(Restrictions.eq("username", user.getUsername()))
					.uniqueResult();
			if(ouser!=null){
				addError("Duplication error",
						"Username already exists on the system. " + user.getUsername(),
						FacesContext.getCurrentInstance());
				return;
			}
		} catch (NonUniqueObjectException e) {
			addError("Duplication error",
					"More than one user was found in the database with username " + user.getUsername(),
					FacesContext.getCurrentInstance());
		}
		
		session.save(user);

		if(userroles!=null && !userroles.trim().isEmpty()){
			String[] customroles = userroles.split(";");
			for (String role : customroles) {
				UserRole userrole = new UserRole();
				userrole.setRoleName(role.trim());
				userrole.setUserLog(user);
				userrole.setValid(true);
				session.save(userrole);
			}
		}
		if(roles!=null)
			for(String role : roles){
				UserRole userrole = new UserRole();
				userrole.setRoleName(role.trim());
				userrole.setUserLog(user);
				userrole.setValid(true);
				session.save(userrole);
			}
		
		addMessage("Success", "User added successfully", ctx);
		user = new UserLog();
	}

	
	public void deleteUser(ActionEvent event) {
		 if(user.getUsername().equals("administrator")){
			 addError("Invalid operation", "You cannot delete this user", FacesContext.getCurrentInstance());
			 return;
		 }
			 
		
		Session session = HibernateHelper.getCurrentSession();
		if(user !=null && user.getId()>0){
			session.delete(user);
			setOutcome("success");
			addMessage("User deleted", "User has been successfully deleted.", FacesContext.getCurrentInstance());
		} else {
			setOutcome("error");
			addMessage("Invalid user", "Object returned was not a valid user.", FacesContext.getCurrentInstance());
		}
	}
	
	public void updateUser(ActionEvent event) {
		Session session = HibernateHelper.getCurrentSession();
		if(user !=null && user.getId()>0){
			session.update(user);
			setOutcome("success");
			addMessage("User modified", "User has been successfully updated.", FacesContext.getCurrentInstance());
		} else {
			setOutcome("error");
			addMessage("Invalid user", "Object returned was not a valid user.", FacesContext.getCurrentInstance());
		}
	}
}
