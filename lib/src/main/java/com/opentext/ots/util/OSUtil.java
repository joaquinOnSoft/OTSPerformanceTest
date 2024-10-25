package com.opentext.ots.util;


public class OSUtil {

	public static String getOperatingSystem() {
	    String os = System.getProperty("os.name");
	    return os;
	}
	
	
	public static boolean isWindows() {
		return isOS("win");		
	}
	
	public static boolean isLinux() {
		return isOS("linux");
	}
	
	private static boolean isOS(String osAlias) {
		boolean win = false;
		
		String os = getOperatingSystem();
		if (os != null && os.toLowerCase().contains(osAlias)) {
			win = true;
		}
		
		return win;
	}
}