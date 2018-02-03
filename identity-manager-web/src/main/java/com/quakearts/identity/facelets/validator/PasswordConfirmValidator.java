package com.quakearts.identity.facelets.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import com.quakearts.identity.facelets.input.PasswordHashingComponent;

public class PasswordConfirmValidator  implements Validator{
	/**
	 * 
	 */
	private final PasswordHashingComponent passwordHashingComponent;

	/**
	 * @param passwordHashingComponent
	 */
	public PasswordConfirmValidator(PasswordHashingComponent passwordHashingComponent) {
		this.passwordHashingComponent = passwordHashingComponent;
	}

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		if(!Boolean.parseBoolean(this.passwordHashingComponent.get("validating")))
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