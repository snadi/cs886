package ca.uwaterloo.bhp.util;

import soot.ClassProvider;
import soot.Scene;

public class SootConfig {
	
	public static void addCommonDynamicClasses(Scene scene, ClassProvider provider) {
		/* For simulating the FileSystem class, we need the implementation
	       of the FileSystem, but the classes are not loaded automatically
	       due to the indirection via native code.
	     */
	    addCommonDynamicClass(scene, provider, "java.io.UnixFileSystem");
	    addCommonDynamicClass(scene, provider, "java.io.WinNTFileSystem");
	    addCommonDynamicClass(scene, provider, "java.io.Win32FileSystem");
	    
	    /* java.net.URL loads handlers dynamically */
	    addCommonDynamicClass(scene, provider, "sun.net.www.protocol.file.Handler");
	    addCommonDynamicClass(scene, provider, "sun.net.www.protocol.ftp.Handler");
	    addCommonDynamicClass(scene, provider, "sun.net.www.protocol.http.Handler");
	    addCommonDynamicClass(scene, provider, "sun.net.www.protocol.https.Handler");
	    addCommonDynamicClass(scene, provider, "sun.net.www.protocol.jar.Handler");
	}
	
	public static void addCommonDynamicClass(Scene scene, ClassProvider provider, String className) {
		if (provider.find(className) != null) {
			scene.addBasicClass(className);
		}
	}

}
