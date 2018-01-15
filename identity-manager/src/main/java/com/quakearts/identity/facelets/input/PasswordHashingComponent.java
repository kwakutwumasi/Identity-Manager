package com.quakearts.identity.facelets.input;

import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import com.quakearts.webapp.facelets.util.ObjectExtractor;
import com.quakearts.webapp.security.util.HashPassword;

public class PasswordHashingComponent extends UIInput {
	private HashPassword hashPassword = new HashPassword();
	
	public PasswordHashingComponent() {
		addValidator(new PasswordConfirmValidator());
	}
	
	public void prepareHashPassword(String alg, String salt, int iterations){
		hashPassword.setAlgorithm(alg);
		hashPassword.setSalt(salt);
		hashPassword.setIterations(iterations);
	}
	
	@Override
	public void restoreState(FacesContext ctx, Object stateobj) {
		Object[] state = (Object[])stateobj;
		hashPassword = (HashPassword) state[0];
		super.restoreState(ctx, state[1]);
	}

	@Override
	public Object saveState(FacesContext context) {
		Object parentState = super.saveState(context);
		Object[] state = new Object[2];
		state[0] = hashPassword;
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
		
		if(value instanceof ValueExpression){
			value = ((ValueExpression)value).getValue(FacesContext.getCurrentInstance().getELContext());			
			hashPassword.setPlainText((String)value);
		}
		hashPassword.setPlainText(value.toString());
		super.setValue(hashPassword);
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

	protected class PasswordConfirmValidator  implements Validator{
		@Override
		public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
			if(!Boolean.parseBoolean(get("validating")))
				return;
			
			String checkValue = context.getExternalContext().getRequestParameterMap().get(component.getClientId(context)+"_validate");
			if(checkValue==null)
				throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Validation entry is null", "No validation entry was passed with the form."));
			
			if(value==null)
				throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Password entry is null", "Password entry is null."));
			
			if(!value.equals(checkValue))
				throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Invalid pasword", "Passwords do not match."));
		}
	}
}
