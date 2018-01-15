package com.quakearts.identity.facelets.input;

import java.util.Properties;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.view.facelets.ComponentConfig;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;

import com.quakearts.identity.facelets.util.IdentityConfig;

public class PasswordHashingComponentHandler extends ComponentHandler {

	public PasswordHashingComponentHandler(ComponentConfig config) {
		super(config);
	}

	@Override
	public void onComponentCreated(FaceletContext ctx, UIComponent c,
			UIComponent parent) {
		if(! (c instanceof PasswordHashingComponent))
			throw new FacesException("Component must be of type PasswordHashingComponent");
		
		PasswordHashingComponent component = ((PasswordHashingComponent)c);
		Properties props = IdentityConfig.getIdentityProperties();
		String alg = props.getProperty("hash.alg");
		if(alg==null)
			alg=IdentityConfig.DEFAULT_ALG;

		String salt = props.getProperty("hash.salt");
		if(salt==null)
			salt=IdentityConfig.DEFAULT_SALT;
			
		int iterations;
		try {
			iterations = Integer.parseInt(props.getProperty("hash.iterations"));
		} catch (Exception e) {
			iterations = IdentityConfig.DEFAULT_ITERATIONS;
		}
		component.prepareHashPassword(alg, salt, iterations);
	}	
}
