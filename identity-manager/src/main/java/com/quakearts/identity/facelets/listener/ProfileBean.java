package com.quakearts.identity.facelets.listener;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.quakearts.identity.hibernate.UserLog;
import com.quakearts.webapp.facelets.tag.BaseBean;
import com.quakearts.webapp.hibernate.HibernateHelper;

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
			Session session = HibernateHelper.getCurrentSession();
			
			String username = ctx.getExternalContext().getRemoteUser();
			
			if(username !=null && username.trim().length()>0){
				Criteria query = session.createCriteria(UserLog.class).add(Restrictions.eq("username", username));
				user = (UserLog) query.uniqueResult();
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
