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
import ca.uwaterloo.bhp.weka.ArffWriter;

public class DataPreprocessor {
	
	private static NoSearchingClassProvider _provider;
	private static Scene _scene;
	
	public static final String INPUT_DIRECTORY = System.getProperty("user.dir") + File.separator + "cs886" + File.separator + "input";
	public static final String ARFF_DIRECTORY = System.getProperty("user.dir") + File.separator + "cs886" + File.separator + "arff";
	
	public static void run() throws IOException {
		// Initialize some parameters for Soot
		init();
		
		// Load the classes
		Collection<SootClass> inputClasses = loadClasses();
		
		// Generate the features
		generateFeatures(inputClasses);		
	}
	
	private static void generateFeatures(Collection<SootClass> inputClasses) throws IOException {
		// For each soot class, generate control flow graphs for its methods, and process them
		for(SootClass sc : inputClasses){
			for(BriefBlockGraph cfg : CfgGenerator.generate(sc)){
				Collection<ExecutionPath> paths = CfgWalker.process(cfg);
				for(ExecutionPath path : paths) {
					FeatureExtractor.extractFeatures(path);
				}
				
				ArffWriter writer = new ArffWriter(ARFF_DIRECTORY, sc.getName().replaceAll("\\.", "_"), cfg.getBody().getMethod().getNumber(), paths);
				writer.write();
			}
		}
	}
	
	private static SootClass loadClassByCanonicalName(String className) {
		_scene.loadClassAndSupport(className);
	    _scene.loadNecessaryClasses();
		SootClass sc = _scene.loadClass(className, SootClass.BODIES);
		sc.setApplicationClass();
		
		return sc;
	}
	
	private static Collection<SootClass> loadSootClasses() {
		// Load SootClasses for the input files
		Collection<SootClass> classes = new ArrayList<SootClass>();
	    for(String className : _provider.getClassNames()) {
	    	//_scene.loadClassAndSupport(className);
	    	SootClass sc = _scene.loadClass(className, SootClass.BODIES);
	    	_scene.loadNecessaryClasses();
	    	sc.setApplicationClass();
	    	classes.add(sc);
	    }
	    
	    // Set the class provider for Soot
		soot.SourceLocator.v().setClassProviders(Collections.singletonList((ClassProvider) _provider));
		
	    //addCommonDynamicClasses(_scene, _provider);
		_scene.loadNecessaryClasses();
		
		return classes;
	}
	
	private static Collection<SootClass> loadClasses() throws IOException {
		// Fetch the input files
		for(File file : new File(INPUT_DIRECTORY).listFiles()) {
    		if(file.getName().endsWith(".jar") || file.getName().endsWith(".zip")) {
    	        System.out.println("Adding archive: " + file.getName());
    	        _provider.addArchive(file);
    		} else if (file.getName().endsWith(".class")) {
    			System.out.println("Adding file: " + file.getName());
    	        _provider.addClass(file);
    		}
    	}
		
		return loadSootClasses();
	}
	
	private static void init() {
		// Reset Soot
		G.reset();
		
		// Create the Soot-related members
		_provider = new NoSearchingClassProvider();
		_scene = Scene.v();
	}
}