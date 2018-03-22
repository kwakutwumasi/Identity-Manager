package com.quakearts.identity.test;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;
import java.util.Properties;
import java.util.Random;

import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsNull.*;

import org.junit.Test;

import com.quakearts.identity.util.IdentityConfig;

public class TestIdentityConfig {
	
	@Test
	public void testConfig() throws Exception {
		assertThat(IdentityConfig.findFile("default.configuration"), is(notNullValue()));
		File testFile = new File("test.file");
		if(testFile.exists())
			testFile.delete();
		
		URL testFileUrl = IdentityConfig.findFile("test.file");
		assertThat(testFileUrl, is(notNullValue()));
		testFile = new File("test.file");
		assertThat(testFile.exists(), is(true));
		testFile.deleteOnExit();
	}
	
	@Test
	public void testIdentityConfigSave() throws Exception {		
		Properties properties = IdentityConfig.getIdentityProperties();
		String random = Long.toHexString(new Random().nextLong());
		properties.put("test", random);		
		IdentityConfig.saveIdentityProperties();
		IdentityConfig.reset();
		
		assertThat(IdentityConfig.getIdentityProperties().getProperty("test"), is(random));		
	}
}
