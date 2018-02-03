package com.quakearts.identity.facelets.listener;

import java.util.ArrayList;
import java.util.Properties;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import com.quakearts.identity.util.IdentityConfig;
import com.quakearts.webapp.facelets.base.BaseBean;

@ManagedBean(name="systemManagement")
@ViewScoped
public class SystemManagementBean extends BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 759999130827274402L;
	private String application;
	private String applicationName;
	private String role;

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	
	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public SelectItem[] getApplications() {
		Properties props = IdentityConfig.getIdentityProperties();
		String applicationsString = props.getProperty("identity.applications");
		ArrayList<SelectItem> itemsList = new ArrayList<SelectItem>();
		if(applicationsString!=null && !applicationsString.trim().isEmpty()){
			for(String appcombined: applicationsString.trim().split(";")){
				String[] nameValue = appcombined.split("\\|",2);
				if(nameValue.length<2)
					continue;
				
				SelectItem item = new SelectItem(nameValue[0], nameValue[1]);
				itemsList.add(item);
			}
		}
		return itemsList.toArray(new SelectItem[itemsList.size()]);
	}
	
	public String[] getRoles() {
		if(application!=null){
			Properties props = IdentityConfig.getIdentityProperties();
			String rolesstring = props.getProperty(application);
			
			if(rolesstring!=null && !rolesstring.trim().isEmpty()){
				return rolesstring.trim().split(";");
			}
		}
		return new String[0];
	}
	
	public void addApplication(ActionEvent event){
		if(applicationName==null)
			return;
		
		String application = applicationName.replaceAll("\\s","").toLowerCase();
		Properties props = IdentityConfig.getIdentityProperties();
		String applicationsString = props.getProperty("identity.applications");
		StringBuilder builder= new StringBuilder();
		if(applicationsString!=null){
			if(applicationsString.contains(applicationName)){
				addError("Duplicate application", "The application already exists", FacesContext.getCurrentInstance());
				return;
			}
			
			builder.append(applicationsString).append(";");
		}
		
		builder.append(application).append("|").append(applicationName);
		props.setProperty("identity.applications", builder.toString());
		
		if(IdentityConfig.saveIdentityProperties())
			addMessage("Success", "Application has been added", FacesContext.getCurrentInstance());
		else {
			props.setProperty("identity.applications", applicationsString);
			addError("Error", "Identity properties could not saved", FacesContext.getCurrentInstance());
		}
	}

	public void addRoleForApplication(ActionEvent event){
		if(role==null){
			addError("No action taken", "No role has been selected", FacesContext.getCurrentInstance());
			return;
		}
		
		if(application==null){
			addError("No action taken", "No application has been selected", FacesContext.getCurrentInstance());
			return;
		}

		Properties props = IdentityConfig.getIdentityProperties();
		String rolesstring = props.getProperty(application);
		StringBuilder builder= new StringBuilder();
		if(rolesstring!=null){
			if(rolesstring.contains(role)){
				addError("Duplicate role", "The role already exists", FacesContext.getCurrentInstance());
				return;
			}
			builder.append(rolesstring).append(";");
		}
		
		builder.append(role);
		
		props.setProperty(application, builder.toString());
		
		if(IdentityConfig.saveIdentityProperties())
			addMessage("Success", "Role has been added", FacesContext.getCurrentInstance());
		else
			addError("Error", "Identity properties could not be saved", FacesContext.getCurrentInstance());
		
	}
	
	public void removeApplication(ActionEvent event){
		if(application==null){
			addError("No action taken", "No application has been selected", FacesContext.getCurrentInstance());
			return;
		}

		Properties props = IdentityConfig.getIdentityProperties();
		String applicationsString = props.getProperty("identity.applications");
		if(applicationsString !=null && applicationsString.contains(application)){
			int start = applicationsString.indexOf(application);
			int end = applicationsString.indexOf(";", start);
			if(end==-1)
				end = applicationsString.length()-1;
			else
				end++;

			String applicationEntry = applicationsString.substring(start,end);
			
			props.setProperty("identity.applications", applicationsString.replace(applicationEntry, ""));
			
			if(props.containsKey(application)){
				props.remove(application);
			}
			
			if(IdentityConfig.saveIdentityProperties())
				addMessage("Success", "Application has been removed", FacesContext.getCurrentInstance());
			else
				addError("Error", "Identity properties could not saved", FacesContext.getCurrentInstance());
		} else {
			addError("No action taken", "The selected application has not been found", FacesContext.getCurrentInstance());
		}
		application=null;
	}
	
	public void removeRole(ActionEvent event){
		if(role==null){
			addError("No action taken", "No role has been selected", FacesContext.getCurrentInstance());
			return;
		}
		
		if(application==null){
			addError("No action taken", "No application has been selected", FacesContext.getCurrentInstance());
			return;
		}
		
		Properties props = IdentityConfig.getIdentityProperties();
		String rolesstring = props.getProperty(application);
		
		if(rolesstring!=null && rolesstring.contains(role)){
			props.setProperty(application, rolesstring.replace(role, "").replace(";;", ";"));
			
			if(IdentityConfig.saveIdentityProperties())
				addMessage("Success", "Role has been removed", FacesContext.getCurrentInstance());
			else
				addError("Error", "Identity properties could not be saved", FacesContext.getCurrentInstance());

		} else {
			addError("No action taken", "The selected role has not been found", FacesContext.getCurrentInstance());
		}

	}
	
}
