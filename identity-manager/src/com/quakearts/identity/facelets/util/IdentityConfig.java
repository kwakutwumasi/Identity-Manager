package com.quakearts.identity.facelets.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;

public class IdentityConfig {
	private static Properties identityProperties;
	private static Logger log= Logger.getLogger(IdentityConfig.class.getName());
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
				URL url = Thread.currentThread().getContextClassLoader().getResource("identity.properties");
				OutputStream os = new FileOutputStream(new File(url.toURI()));
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
