package com.quakearts.identity.system;

import javax.faces.application.Application;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PostConstructApplicationEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;

import com.quakearts.identity.IdentityManagerBootstrap;

public class AdministratorCreateEvent implements SystemEventListener {

	@Override
	public void processEvent(SystemEvent event) throws AbortProcessingException {
		if (event instanceof PostConstructApplicationEvent) {			
			new IdentityManagerBootstrap().boostrapIdentityModule();
		}
	}

	@Override
	public boolean isListenerForSource(Object source) {
	      return (source instanceof Application);
	}

}
