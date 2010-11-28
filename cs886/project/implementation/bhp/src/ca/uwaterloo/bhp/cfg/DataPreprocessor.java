package ca.uwaterloo.bhp.cfg;

import soot.Scene;
import soot.SootClass;
import soot.toolkits.graph.BriefBlockGraph;

public class DataPreprocessor {
	
	public static BriefBlockGraph constructCFG() {
	    Scene.v().loadClassAndSupport("java.lang.String");
	    Scene.v().loadNecessaryClasses();
		SootClass sc = Scene.v().getSootClass("java.lang.String");
		sc.setApplicationClass();
		return new BriefBlockGraph(sc.getMethodByName("charAt").retrieveActiveBody());
	}
	
	public static void main(String args[]) {
		BriefBlockGraph cfg = constructCFG();
		
		System.out.println(cfg.getHeads());
	}

}
