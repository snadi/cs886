package ca.uwaterloo.bhp.cfg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import soot.toolkits.graph.Block;
import weka.core.Attribute;
import weka.core.FastVector;

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
	
	public double[] featuresToArray() {
		double[] result = new double[features.size()];
		int i = 0;
		for(Feature feature : features.values()) {
			result[i++] = feature.getCount();
		}
		return result;
	}
	
	public static FastVector getAttributes(HashMap<FeatureName, Feature> features) {
		FastVector attributes = new FastVector(features.size() + 1);
		
		// Add attributes for all features
		for(FeatureName featureName : features.keySet()) {
			attributes.addElement(new Attribute(featureName.name()));
		}
		
		// Add the nominal attribute "class"
		FastVector nominalValues = new FastVector(2);
		nominalValues.addElement("hot");
		nominalValues.addElement("cold");
		Attribute classification = new Attribute("class", nominalValues);
		attributes.addElement(classification);
		
		return attributes;
	}
	
}
