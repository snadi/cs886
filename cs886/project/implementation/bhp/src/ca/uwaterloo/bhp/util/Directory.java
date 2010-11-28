package ca.uwaterloo.bhp.util;

import java.io.File;

public class Directory {
	
	public static boolean exists(String d) {
    	File dir = new File(d);
    	
		if(!dir.exists()) {
    		System.err.println("Directory " + d + " does not exist.");
    		return false;
    	} else if(!dir.isDirectory()) {
    		System.err.println(d + " is not a directory.");
    		return false;
    	}
		
		return true;
	}
	
	public static void createDirectory(String path) {
		File dir = new File(path);
		
		if(!exists(path) && !dir.mkdirs()) {
			System.err.println("Can't create directory " + path + ".");
			System.exit(0);
		}
	}
	
}
