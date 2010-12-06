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
import soot.SootMethod;
import soot.toolkits.graph.BriefBlockGraph;
import weka.core.converters.ArffSaver;
import ca.uwaterloo.bhp.cfg.CfgGenerator;
import ca.uwaterloo.bhp.cfg.CfgWalker;
import ca.uwaterloo.bhp.cfg.ExecutionPath;
import ca.uwaterloo.bhp.cfg.FeatureExtractor;

public class DataPreprocessor {
	
	private static NoSearchingClassProvider _provider;
	private static Scene _scene;
	
	public static void main(String args[]) {
		init();
		SootClass sc = loadClassByCanonicalName("java.lang.String");
		
		SootMethod m = new SootMethod("", sc.getMethodByName("charAt").getParameterTypes(), sc.getMethodByName("charAt").getReturnType());
		for(SootMethod method : sc.getMethods()) {
			if(method.getName().equalsIgnoreCase("charAt")) {
				m = method;
				break;
			}
		}
		String outputDirectory = System.getProperty("user.dir") + File.separator + "cs886";
		BriefBlockGraph cfg = CfgGenerator.generate(m);
		
		System.out.println(sc.getName().replaceAll("\\.", "_"));
		System.out.println(cfg.getBody().getMethod().getNumber());
		
		for(ExecutionPath path : CfgWalker.process(cfg)) {
			//System.out.println(path.pathToString());
			FeatureExtractor.extractFeatures(path);
			System.out.println(path.featuresToString());
		}
	}
	
	public static void run(String inputDirectory, String outputDirectory) throws IOException {
		// Initialize some parameters for Soot
		init();
		
		// Load the classes
		Collection<SootClass> inputClasses = loadClasses(inputDirectory);
		
		// Generate the features
		generateFeatures(inputClasses);		
	}
	
	private static void generateFeatures(Collection<SootClass> inputClasses) {
		// For each soot class, generate cfgs for its methods, and process them
		for(SootClass sc : inputClasses){
			for(BriefBlockGraph cfg : CfgGenerator.generate(sc)){
				Collection<ExecutionPath> paths = CfgWalker.process(cfg);
				sc.getName();
				cfg.getBody().getMethod().getName();
				for(ExecutionPath path : paths) {
					//System.out.println(path.pathToString());
					FeatureExtractor.extractFeatures(path);
					System.out.println(path.featuresToString());
				}
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
	    	_scene.loadClass(className, SootClass.SIGNATURES);
	    	//_scene.loadClassAndSupport(className);
	    	SootClass sc = _scene.loadClass(className, SootClass.BODIES);
	    	sc.setApplicationClass();
	    	classes.add(sc);
	    }
	    
	    // Set the class provider for Soot
		soot.SourceLocator.v().setClassProviders(Collections.singletonList((ClassProvider) _provider));
		
	    //addCommonDynamicClasses(_scene, _provider);
		_scene.loadNecessaryClasses();
		
		return classes;
	}
	
	private static Collection<SootClass> loadClasses(String inputDirectory) throws IOException {
		// Fetch the input files
		for(File file : new File(inputDirectory).listFiles()) {
    		if(file.getName().endsWith(".jar") || file.getName().endsWith(".zip")) {
    	        System.out.println("Adding archive: " + file.getName());
    	        _provider.addArchive(file);
    		} else if (file.getName().endsWith(".java") || file.getName().endsWith(".class")) {
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
