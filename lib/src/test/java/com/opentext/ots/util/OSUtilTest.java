package com.opentext.ots.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class OSUtilTest {

	@Test 
	void getOperatingSystem() {
		assertNotNull(OSUtil.getOperatingSystem());
	}
	
	@Test
	public void isLinux() {
		String os = OSUtil.getOperatingSystem();
		
		assertNotNull(os);
		
		if(os.toLowerCase().contains("linux")) {
			assertTrue(OSUtil.isLinux());
		}
	}
	
	@Test
	public void isWindows() {
		String os = OSUtil.getOperatingSystem();
		
		assertNotNull(os);
		
		if(os.toLowerCase().contains("win")) {
			assertTrue(OSUtil.isWindows());
		}
	}
}