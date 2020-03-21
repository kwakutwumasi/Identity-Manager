package com.quakearts.identity.facelets.listener;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import com.quakearts.identity.model.UserLog;
import com.quakearts.identity.model.UserRole;
import com.quakearts.webapp.facelets.base.BaseBean;
import com.quakearts.webapp.orm.DataStoreFactory;
import com.quakearts.webapp.orm.query.criteria.CriteriaMapBuilder;

@Named("search")
@ViewScoped
public class SearchUsersBean extends BaseBean {

	private static final String VALID = "valid";
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

			CriteriaMapBuilder builder = CriteriaMapBuilder.createCriteria();
			
			if (status != null && !status.trim().isEmpty()) {
				filterByInvalidity(builder);
			}

			if (username != null && !username.trim().isEmpty())
				builder.property("username").mustBeLike(username);

			users = DataStoreFactory.getInstance().getDataStore().find(UserLog.class)
					.using(builder.finish())
					.thenList();
			if(roles!=null && !roles.trim().isEmpty()){
				users = filterByRoles(users);
			}
			users = new LinkedHashSet<>(users);
		}

		return users;
	}

	private void filterByInvalidity(CriteriaMapBuilder builder) {
		if (status.equals(VALID))
			builder.property(VALID).mustBeEqualTo(true);
		else if (status.equals("invalid"))
			builder.property(VALID).mustBeEqualTo(false);
	}

	private Collection<UserLog> filterByRoles(Collection<UserLog> users) {
		String[] rolesArr = roles.split(";");
		Collection<String> rolesSet = Arrays.asList(rolesArr);
		return users.stream().filter(userLog->{
			Set<String> userroles = userLog.getUserRoles()
					.stream().map(UserRole::getRoleName).collect(Collectors.toSet());
			return userroles.containsAll(rolesSet);
		}).collect(Collectors.toList());
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
