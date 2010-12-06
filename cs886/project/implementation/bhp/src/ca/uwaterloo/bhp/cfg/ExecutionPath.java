package ca.uwaterloo.bhp.cfg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import soot.toolkits.graph.Block;

public class ExecutionPath {
	
	private HashMap<FeatureName, Feature> features;
	private Collection<Block> blocks;
	
	public ExecutionPath() {
		features = new HashMap<FeatureName, Feature>();
		
		// Add a feature for each feature name
		for(FeatureName key : FeatureName.values()) {
			features.put(key, new Feature(key));
		}
		
		blocks = new ArrayList<Block>();
	}

	public HashMap<FeatureName, Feature> features() {
		return features;
	}
	
	public Collection<Block> blocks() {
		return blocks;
	}
	
	public String pathToString() {
		StringBuffer str = new StringBuffer();
		for(Block b : blocks) {
			str.append(b.getIndexInMethod());
			str.append(", ");
		}
		return str.replace(str.lastIndexOf(","), str.lastIndexOf(" "), "").toString();
	}
	
	public String featuresToString() {
		StringBuffer str = new StringBuffer();
		for(Feature feature : features.values()) {
			//str.append(feature.getName());
			//str.append("=");
			str.append(feature.getCount());
			str.append(", ");
		}
		return str.replace(str.lastIndexOf(","), str.lastIndexOf(" "), "").toString();
	}
	
}
