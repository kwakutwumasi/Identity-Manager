package com.quakearts.identity;

import com.quakearts.appbase.cdi.annotation.TransactionParticipant;
import com.quakearts.appbase.cdi.annotation.TransactionParticipant.TransactionType;

public class Main {
	
	@TransactionParticipant(TransactionType.SINGLETON)
	public void init() {
		new IdentityManagerBootstrap().boostrapIdentityModule();
	}	
}
