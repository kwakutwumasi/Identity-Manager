package com.quakearts.identity.facelets.util;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import com.quakearts.identity.facelets.listener.SystemManagementBean;

public class IdentityConfig {
	private static Properties identityProperties;
	private static Logger log= Logger.getLogger(SystemManagementBean.class.getName());
	public static final String DEFAULT_ALG="MD5";
	public static final String DEFAULT_SALT ="Q@(b+Ux3";
	public static final int DEFAULT_ITERATIONS = 101;
		
	private IdentityConfig() {
	}
	
	public static Properties getIdentityProperties(){
		if(identityProperties==null){
			 identityProperties= new Properties();
			 InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("identity.properties");
			 try {
				identityProperties.load(is);
			} catch (Exception e) {
				log.severe("Exception of type " + e.getClass().getName() + " was thrown. Message is " + e.getMessage()
						+ ". Exception occured whiles loading identity.properties");
				identityProperties = null;
			}
			return identityProperties;
		} else{
			return identityProperties;
		}
	}
	
	public static synchronized boolean saveIdentityProperties(){
		if(identityProperties!=null){
			try {
				ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
				OutputStream os = new FileOutputStream(ctx.getRealPath("WEB-INF/classes/identity.properties"));
				identityProperties.store(os, "Identity Properties");
				return true;
			} catch (Exception e) {
				log.severe("Exception of type " + e.getClass().getName() + " was thrown. Message is " + e.getMessage()
						+ ". Exception occured whiles saving identity.properties");
			}
		}
		return false;
	}
}
