package ca.uwaterloo.bhp.cfg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import soot.toolkits.graph.Block;
import weka.core.Attribute;
import weka.core.FastVector;
import ca.uwaterloo.bhp.feature.Feature;
import ca.uwaterloo.bhp.feature.FeatureName;
import ca.uwaterloo.bhp.util.ClustersAverages;

public class ExecutionPath {
	
	private HashMap<FeatureName, Feature> features;
	private Collection<Block> blocks;
	
	private static final String HOT = "hot";
	private static final String COLD = "cold";
	
	private static final double featureContribution = 1d / FeatureName.values().length;
	
	private Attribute classAttribute;
	
	public ExecutionPath() {
		features = new HashMap<FeatureName, Feature>();
		
		// Add a feature for each feature name
		for(FeatureName key : FeatureName.values()) {
			features.put(key, new Feature(key));
		}
		
		blocks = new ArrayList<Block>();
		
		// Create the nominal attribute "class"
		FastVector nominalValues = new FastVector(2);
		nominalValues.addElement(HOT);
		nominalValues.addElement(COLD);
		classAttribute = new Attribute("class", nominalValues);
	}

	public HashMap<FeatureName, Feature> features() {
		return features;
	}
	
	public Collection<Block> blocks() {
		return blocks;
	}
	
	public String toString() {
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
			str.append(feature.getCount());
			str.append(", ");
		}
		return str.replace(str.lastIndexOf(","), str.lastIndexOf(" "), "").toString();
	}
	
	public double[] featuresToArray() {
		double[] result = new double[features.size() + 1];
		int i = 0;
		for(Feature feature : features.values()) {
			result[i++] = feature.getCount();
		}
		return result;
	}
	
	public double isHot(int cfgNodeThreshold) {
		double prob = 0;
		for(FeatureName featureName : features.keySet()) {
			if(features.get(featureName).getCount() >= ClustersAverages.getHotPathAverage(featureName, cfgNodeThreshold)) {
				prob += featureContribution;
			}
		}
		// The less feature contribution a path has, the more hot the path is
		return (prob <= 0.5 ? classAttribute.indexOfValue(HOT) : classAttribute.indexOfValue(COLD));
	}
	
	public FastVector getAttributes() {
		FastVector attributes = new FastVector(features.size() + 1);
		
		// Add attributes for all features
		for(FeatureName featureName : features.keySet()) {
			attributes.addElement(new Attribute(featureName.name()));
		}
		
		// Add class attribute
		attributes.addElement(classAttribute);
		return attributes;
	}
}
