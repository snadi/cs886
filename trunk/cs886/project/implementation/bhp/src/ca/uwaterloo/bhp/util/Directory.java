package ca.uwaterloo.bhp.util;

import java.io.File;

public class Directory {
	
	public static final String DIRECTORY = System.getProperty("user.dir") + File.separator + "cs886";
	public static final String INPUT_DIRECTORY = DIRECTORY + File.separator + "input";
	public static final String LIBRARY_DIRECTORY = INPUT_DIRECTORY + File.separator + "lib";
	public static final String ARFF_DIRECTORY = DIRECTORY + File.separator + "arff";
	public static final String OBSERVATIONS_DIRECTORY = ARFF_DIRECTORY + File.separator + "observations";
	public static final String CLUSTERS_DIRECTORY = ARFF_DIRECTORY + File.separator + "clusters";
	
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
