package com.quakearts.identity.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdentityConfig {
	private static Properties identityProperties;
	private static URL identityPropertiesUrl;
	private static Logger log= LoggerFactory.getLogger(IdentityConfig.class.getName());
	private static final String DEFAULT_ALG="SHA-256";
	private static final String DEFAULT_SALT ="Q@(b+Ux3";
	private static final int DEFAULT_ITERATIONS = 101;
	private static int iterations;
		
	private IdentityConfig() {
	}
	
	public static Properties getIdentityProperties(){
		if (identityProperties == null) {
			identityProperties = new Properties();
			try {
				identityPropertiesUrl = findFile("identity.properties");
			} catch (IOException e) {
				log.error("Exception of type " + e.getClass().getName() + " was thrown. Message is " + e.getMessage()
				+ ". Exception occured whiles loading identity.properties");
				throw new IdentityConfigRuntimeException(e);
			}
			
			try (InputStream is = identityPropertiesUrl.openStream()) {
				if (is != null)
					identityProperties.load(is);
			} catch (IOException e) {
				log.error("Exception of type " + e.getClass().getName() + " was thrown. Message is " + e.getMessage()
						+ ". Exception occured whiles loading identity.properties");
				throw new IdentityConfigRuntimeException(e);
			}
			
			return identityProperties;
		} else {
			return identityProperties;
		}
	}

	public static URL findFile(String identityFileName) throws IOException {
		File indentityFile = new File(identityFileName);
		URL identityFileUrl = indentityFile.exists()? indentityFile.toURI().toURL():Thread.currentThread().getContextClassLoader()
				.getResource(identityFileName);
		if(identityFileUrl == null) {
			indentityFile.createNewFile();
			identityFileUrl = indentityFile.toURI().toURL();
		}
		
		return identityFileUrl;
	}
	
	public static synchronized boolean saveIdentityProperties(){
		if(identityPropertiesUrl!=null){
			try(OutputStream os = new FileOutputStream(URLDecoder.decode(identityPropertiesUrl.getFile(),"UTF-8"));) {
				identityProperties.store(os, "Identity Properties");
				return true;
			} catch (Exception e) {
				log.error("Exception of type " + e.getClass().getName() + " was thrown. Message is " + e.getMessage()
						+ ". Exception occured whiles saving identity.properties");
			}
		}
		return false;
	}
	
	public static String getAlgorithm() {
		return getIdentityProperties().getProperty("hash.alg", DEFAULT_ALG);
	}
	
	public static String getSalt() {
		return getIdentityProperties().getProperty("hash.salt", DEFAULT_SALT);		
	}
	
	public static int getIterations() {
		if(iterations <= 0)
			try {
				iterations = Math.abs(Integer.parseInt(getIdentityProperties().getProperty("hash.iterations")));
			} catch (NumberFormatException e) {
				iterations = IdentityConfig.DEFAULT_ITERATIONS;
			}
		
		return iterations;
	}
	
	public static void reset() {
		identityProperties = null;
		identityPropertiesUrl = null;
	}
}
