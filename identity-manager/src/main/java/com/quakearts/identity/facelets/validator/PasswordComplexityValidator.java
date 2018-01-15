package com.quakearts.identity.facelets.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

public class PasswordComplexityValidator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {
		
		if(value==null)
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Null password", "Password cannot be null"));
		else if(!value.toString().matches("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!\"#$%&'()*+,\\-./:;<=>?@\\[\\\\\\]\\^_`{|}~]).{8,30})"))
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid password", "Password does not meet complexity requirements."));
			
	}

}
