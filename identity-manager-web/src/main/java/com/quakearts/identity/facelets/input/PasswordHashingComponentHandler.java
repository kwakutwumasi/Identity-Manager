package com.quakearts.identity.facelets.input;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.view.facelets.ComponentConfig;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;

public class PasswordHashingComponentHandler extends ComponentHandler {

	public PasswordHashingComponentHandler(ComponentConfig config) {
		super(config);
	}

	@Override
	public void onComponentCreated(FaceletContext ctx, UIComponent c,
			UIComponent parent) {
		if(! (c instanceof PasswordHashingComponent))
			throw new FacesException("Component must be of type PasswordHashingComponent");
	}	
}
