package com.quakearts.identity.facelets.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

public class PasswordComplexityValidator implements Validator {

	private static final String ERRORMESSAGE = "Password does not meet complexity requirements."
	+ " Please ensure the password is at least 8 characters"
	+ " long, and contains at least one upper case character,"
	+ " one lower case character, one symbol and one number.";

	@Override
	public void validate(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {
		
		if(value!=null 
				&& !value.toString().trim().isEmpty() 
				&& !value.toString().matches("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!\"#$%&'()*+,\\-./:;<=>?@\\[\\\\\\]\\^_`{|}~]).{8,30})"))
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Invalid password",
					ERRORMESSAGE));
			
	}

}
