package com.quakearts.identity.facelets.input;

import javax.el.ValueExpression;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import com.quakearts.identity.facelets.validator.PasswordConfirmValidator;
import com.quakearts.identity.util.IdentityConfig;
import com.quakearts.webapp.facelets.util.ObjectExtractor;
import com.quakearts.webapp.security.util.HashPassword;

public class PasswordHashingComponent extends UIInput {	
	public PasswordHashingComponent() {
		addValidator(new PasswordConfirmValidator(this));
	}
		
	@Override
	public void restoreState(FacesContext ctx, Object stateobj) {
		Object[] state = (Object[])stateobj;
		super.restoreState(ctx, state[1]);
	}

	@Override
	public Object saveState(FacesContext context) {
		Object parentState = super.saveState(context);
		Object[] state = new Object[2];
		state[1] = parentState;
		return state;
	}

	public static final String COMPONENT_FAMILY = "com.quakearts.identity",
	   COMPONENT_TYPE = "com.quakearts.identity.input";

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}
	
	@Override
	public String getRendererType() {
		return PasswordHashingInputRenderer.RENDERER_TYPE;
	}
	
	@Override
	public void setValue(Object value) {
		if(value == null || value.toString().trim().isEmpty())
			return;
		
		String rawValue;
		
		if(value instanceof ValueExpression){
			value = ((ValueExpression)value).getValue(FacesContext.getCurrentInstance().getELContext());			
			rawValue = (String)value;
		} else {
			rawValue = value.toString();
		}
		
		super.setValue(new HashPassword(rawValue, IdentityConfig.getAlgorithm(), 
					IdentityConfig.getIterations(), 
					IdentityConfig.getSalt()));
	}
	
	@Override
	public Object getValue() {
		return "";
	}
	
	public String get(String attribute) {
		String attributeValue = ObjectExtractor
				.extractString(getValueExpression(attribute), getFacesContext()
						.getELContext());
		if (attributeValue == null)
			attributeValue = (String) getAttributes().get(attribute);

		return attributeValue;
	}
}
