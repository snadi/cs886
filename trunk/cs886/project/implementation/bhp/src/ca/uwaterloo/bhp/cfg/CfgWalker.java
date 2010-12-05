package ca.uwaterloo.bhp.cfg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Stack;

import soot.toolkits.graph.Block;
import soot.toolkits.graph.BriefBlockGraph;

public class CfgWalker {
	
	public static Collection<ExecutionPath> process(BriefBlockGraph cfg) {
		Collection<ExecutionPath> result = new ArrayList<ExecutionPath>();
		
		for(Block head : cfg.getHeads()) {
			Stack<Block> path = new Stack<Block>();
			HashSet<Block> seen = new HashSet<Block>();
			
			Collection<ExecutionPath> allPaths = new ArrayList<ExecutionPath>();
			search(head, head, path, seen, allPaths);
			
			result.addAll(allPaths);
		}
		
		return result;
	}
	
	private static void search(Block head, Block block, Stack<Block> path, HashSet<Block> seen, Collection<ExecutionPath> allPaths) {
		if(block.getSuccs().size() == 0) {
			ExecutionPath p = new ExecutionPath();
			p.blocks().add(head);
			if(path.size() > 0) {
				p.blocks().addAll(Arrays.asList(path.toArray(new Block[1])));
			}
			allPaths.add(p);
		}
	    
		seen.clear();
		seen.addAll(Collections.list(path.elements()));
		
		if(stuck(block, path, seen)) {
			return;
		}
		
		for(Block b : block.getSuccs()) {
			path.push(b);
			search(head, b, path, seen, allPaths);
			path.pop();
		}
	}
	
	private static boolean stuck(Block block, Stack<Block> path, HashSet<Block> seen) {
		if(block.getSuccs().size() == 0) {
			return false;
		}
		
		for(Block b : block.getSuccs()) {
			if(!seen.contains(b)) {
				seen.add(b);
				if(!stuck(b, path, seen)) {
					return false;
				}
			}
		}
		return true;
	}
}
