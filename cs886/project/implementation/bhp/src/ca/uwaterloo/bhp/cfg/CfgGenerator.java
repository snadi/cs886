package ca.uwaterloo.bhp.cfg;

import java.util.ArrayList;
import java.util.Collection;

import soot.SootClass;
import soot.SootMethod;
import soot.toolkits.graph.BriefBlockGraph;

public class CfgGenerator {
	
	public static BriefBlockGraph generate(SootMethod method) {
		return new BriefBlockGraph(method.retrieveActiveBody());
	}
	
	public static Collection<BriefBlockGraph> generate(SootClass sc) {
		Collection<BriefBlockGraph> result = new ArrayList<BriefBlockGraph>();
		
		for(SootMethod method : sc.getMethods()) {
			BriefBlockGraph cfg = generate(method);
			result.add(cfg);
		}
		
		return result;
	    
	}
	
	public static Collection<BriefBlockGraph> generate(Collection<SootClass> classes){
		Collection<BriefBlockGraph> result = new ArrayList<BriefBlockGraph>();
		
		for(SootClass sc : classes) {
			Collection<BriefBlockGraph> cfgs = generate(sc);
			result.addAll(cfgs);
		}
		
		return result;
	}
}
