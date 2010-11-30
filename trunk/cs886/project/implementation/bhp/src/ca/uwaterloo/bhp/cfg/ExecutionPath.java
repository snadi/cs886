package ca.uwaterloo.bhp.cfg;

import java.util.HashMap;

public class ExecutionPath {
	
	private HashMap<FeatureName, Feature> features;
	
	public ExecutionPath() {
		features = new HashMap<FeatureName, Feature>();
		
		// Add a feature for each feature name
		for(FeatureName key : FeatureName.values()) {
			features.put(key, new Feature(key));
		}
	}

	public HashMap<FeatureName, Feature> features() {
		return features;
	}
	
	
}
