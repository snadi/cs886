package ca.uwaterloo.bhp.preprocessing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import soot.ClassProvider;
import soot.G;
import soot.Scene;
import soot.SootClass;
import soot.toolkits.graph.BriefBlockGraph;
import ca.uwaterloo.bhp.cfg.CfgGenerator;
import ca.uwaterloo.bhp.cfg.CfgWalker;
import ca.uwaterloo.bhp.cfg.ExecutionPath;
import ca.uwaterloo.bhp.feature.FeatureExtractor;
import ca.uwaterloo.bhp.util.Directory;
import ca.uwaterloo.bhp.weka.ArffWriter;

public class DataPreprocessor {
	
	public static final int[] cfgThresholds = new int[]{10, 20, 30, 40}; 
	
	public static void run() throws IOException {
		// Reset Soot
		G.reset();
		
		// Create the Soot-related members
		NoSearchingClassProvider provider = new NoSearchingClassProvider();
		
		// Fetch the input files
		for(File file : new File(Directory.INPUT_DIRECTORY).listFiles()) {
    		if(file.getName().endsWith(".jar") || file.getName().endsWith(".zip")) {
    	        System.out.println("Adding archive: " + file.getName());
    	        provider.addArchive(file);
    		} else if (file.getName().endsWith(".class")) {
    			System.out.println("Adding file: " + file.getName());
    	        provider.addClass(file);
    		}
    	}
		
		// Fetch all library jars
		for(File file : new File(Directory.LIBRARY_DIRECTORY).listFiles()) {
			provider.addArchiveForResolving(file);
    	}
		
		// Set the class provider for Soot
		soot.SourceLocator.v().setClassProviders(Collections.singletonList((ClassProvider) provider));
		Scene scene = Scene.v();

		Collection<SootClass> classes = new ArrayList<SootClass>();
	    for(String className : provider.getClassNames()) {
	    	scene.loadClass(className, SootClass.SIGNATURES);
	    	SootClass c = scene.loadClass(className, SootClass.BODIES);
	    	c.setApplicationClass();
	    	classes.add(c);
	    }
	    
	    addCommonDynamicClasses(scene, provider);
	    scene.loadNecessaryClasses();

		// Generate the training sets
	    //generateFeatures(classes, 10, false);
//		for(int cfgThreshold : cfgThresholds) {
//			long startTime = System.currentTimeMillis();
//			generateFeatures(classes, cfgThreshold);
//			System.out.println("Observations for " + cfgThreshold + " nodes generated.");
//			System.out.println("It took " + (System.currentTimeMillis() - startTime) + " to finish.");
//		}

		// Generate test set
		long startTime = System.currentTimeMillis();
		generateFeatures(classes, 50);
		System.out.println("Test dataset generated.");
		System.out.println("It took " + (System.currentTimeMillis() - startTime) + " to finish.");
	}
	
	private static void generateFeatures(Collection<SootClass> inputClasses, int cfgNodeThreshold) throws IOException {
		// For each soot class, generate control flow graphs for its methods, and process them
		Collection<ExecutionPath> paths = new ArrayList<ExecutionPath>();
		for(SootClass sc : inputClasses){
			for(BriefBlockGraph cfg : CfgGenerator.generate(sc)){
				if(cfg.size() > cfgNodeThreshold) continue;
				for(ExecutionPath path : CfgWalker.process(cfg)) {
					FeatureExtractor.extractFeatures(path);
					paths.add(path);
				}
			}
		}
		
		if(paths.size() > 0) {
			ArffWriter writer = new ArffWriter(Directory.OBSERVATIONS_DIRECTORY, cfgNodeThreshold, paths);
			writer.write();
		}
	}
	
	private static void addCommonDynamicClasses(Scene scene, ClassProvider provider) {
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
	
	private static void addCommonDynamicClass(Scene scene, ClassProvider provider, String className) {
		if (provider.find(className) != null) {
			scene.addBasicClass(className);
		}
	}
}