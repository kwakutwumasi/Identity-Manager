package com.quakearts.identity.facelets.listener;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Restrictions;
import com.quakearts.identity.hibernate.UserLog;
import com.quakearts.webapp.facelets.base.BaseBean;
import com.quakearts.webapp.hibernate.HibernateHelper;

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
	private List<UserLog> users;

	@SuppressWarnings("unchecked")
	public List<UserLog> getUsers() {
		if (users == null) {

			Session session = HibernateHelper.getCurrentSession();
			Criteria query = session.createCriteria(UserLog.class);

			if (status != null && !status.trim().isEmpty()) {
				if (status.equals("valid"))
					query.add(Restrictions.eq("valid", true));
				else if (status.equals("invalid"))
					query.add(Restrictions.eq("valid", false));
			}

			if (username != null && !username.trim().isEmpty())
				query.add(Restrictions.ilike("username", username));

			if (roles != null && roles.trim().length() > 0) {
				Criteria subquery = query.createCriteria("userRoles");
				String[] rolesArr = roles.split(";");
				if (rolesArr.length > 1) {
					LogicalExpression orExpression = Restrictions.or(Restrictions.eq("roleName", rolesArr[0]),
							Restrictions.eq("roleName", rolesArr[1]));
					for (int i = 2; i < rolesArr.length; i++) {
						orExpression = Restrictions.or(orExpression, Restrictions.eq("roleName", rolesArr[i]));
					}
					subquery.add(orExpression);
				} else {
					subquery.add(Restrictions.eq("roleName", roles));
				}
			}

			users = (List<UserLog>) query.list();
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
