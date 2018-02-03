package com.quakearts.identity.facelets.listener;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import com.quakearts.identity.model.UserLog;
import com.quakearts.webapp.facelets.base.BaseBean;
import com.quakearts.webapp.orm.DataStoreFactory;
import com.quakearts.webapp.orm.query.helper.ParameterMapBuilder;

@ManagedBean(name="search")
@ViewScoped
public class SearchUsersBean extends BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3856652252928084090L;
	private String roles;
	private String status;
	private String username;
	private Collection<UserLog> users;

	public Collection<UserLog> getUsers() {
		if (users == null) {

			ParameterMapBuilder builder = ParameterMapBuilder.createParameters();
			
			if (status != null && !status.trim().isEmpty()) {
				if (status.equals("valid"))
					builder.add("valid", true);
				else if (status.equals("invalid"))
					builder.add("valid", false);
			}

			if (username != null && !username.trim().isEmpty())
				builder.addVariableString("username", username);

			if (roles != null && roles.trim().length() > 0) {
				String[] rolesArr = roles.split(";");
				if (rolesArr.length > 1) {
					builder.disjoin();
					for (String roleName: rolesArr) {
						builder.add("userRole.roleName", roleName);
					}
					builder.endjoin();
				} else {
					builder.add("userRole.roleName", rolesArr[0]);
				}
			}

			users = new LinkedHashSet<>(DataStoreFactory.getInstance().getDataStore().list(UserLog.class, builder.build()));
		}

		return users;
	}

	public void clear(ActionEvent event){
		users = null;
	}
	
	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setUsers(List<UserLog> users) {
		this.users = users;
	}

}
