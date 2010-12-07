package ca.uwaterloo.bhp.cfg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Stack;

import soot.toolkits.graph.Block;
import soot.toolkits.graph.BriefBlockGraph;

public class CfgWalker {
	
	public static Collection<ExecutionPath> process(BriefBlockGraph cfg) {
		Collection<ExecutionPath> result = new ArrayList<ExecutionPath>();
		
		for(Block head : cfg.getHeads()) {
			Stack<Block> path = new Stack<Block>();
			
			Collection<ExecutionPath> allPaths = new ArrayList<ExecutionPath>();
			search(head, path, allPaths);
			
			result.addAll(allPaths);
		}
		return result;
	}

	private static void search(Block block, Stack<Block> path, Collection<ExecutionPath> allPaths) {
		// Check we're not stuck in a cycle
		if(path.contains(block) && path.indexOf(block) != path.lastIndexOf(block)) {
			return;
		}
		
		// Add current block to path
		path.push(block);
		
		// Did we hit a tail of the graph? 
		if(block.getSuccs().size() == 0) {
			ExecutionPath p = new ExecutionPath();
			p.blocks().addAll(Arrays.asList(path.toArray(new Block[1])));
			allPaths.add(p);
			path.pop(); // Required to get all paths correctly
			return;
		}
		
		// Loop over all children
		for(Block succs : block.getSuccs()) {
			search(succs, path, allPaths);
		}
		
		// No path found
		path.pop();
	}
}
