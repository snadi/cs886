package ca.uwaterloo.bhp.cfg;

import java.util.ArrayList;
import java.util.Collection;

import soot.toolkits.graph.Block;
import soot.toolkits.graph.BriefBlockGraph;

public class CfgWalker {
	
	public static Collection<ExecutionPath> process(BriefBlockGraph cfg) {
		Collection<ExecutionPath> result = new ArrayList<ExecutionPath>();
		
		for(Block head : cfg.getHeads()) {
			boolean done = false;
			
			while(!done) {
				head.
			}
		}
		
		return result;
		
	}

}
