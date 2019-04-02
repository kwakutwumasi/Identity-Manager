package com.quakearts.identity;

import com.quakearts.appbase.cdi.annotation.Transactional;
import com.quakearts.appbase.cdi.annotation.Transactional.TransactionType;

public class Main {
	
	@Transactional(TransactionType.SINGLETON)
	public void init() {
		new IdentityManagerBootstrap().boostrapIdentityModule();
	}	
}
