package ca.uwaterloo.bhp.cfg;

import java.util.ArrayList;
import java.util.Collection;

import soot.SootClass;
import soot.SootMethod;
import soot.toolkits.graph.BriefBlockGraph;

public class CfgGenerator {
	
	public static BriefBlockGraph generate(SootMethod method) {
		return method.isConcrete() ? new BriefBlockGraph(method.retrieveActiveBody()) : null;
	}
	
	public static Collection<BriefBlockGraph> generate(SootClass sc) {
		Collection<BriefBlockGraph> result = new ArrayList<BriefBlockGraph>();
		for(SootMethod method : sc.getMethods()) {
			BriefBlockGraph cfg = generate(method);
			if(cfg != null) result.add(cfg);
		}
		
		return result;
	    
	}
}
