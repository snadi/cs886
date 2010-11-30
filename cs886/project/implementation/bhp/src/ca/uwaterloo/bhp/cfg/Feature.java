package ca.uwaterloo.bhp.cfg;

public class Feature {
	
	private FeatureName name;
	private int count;
	private double coverage;
	
	public Feature(FeatureName name) {
		this.setName(name);
		setCount(0);
		setCoverage(0);
	}

	public void setName(FeatureName name) {
		this.name = name;
	}

	public FeatureName getName() {
		return name;
	}

	public void decrementCount() {
		this.count--;
	}
	
	public void decrementCount(int decrement) {
		this.count -= decrement;
	}
	
	public void incrementCount() {
		this.count++;
	}
	
	public void incrementCount(int increment) {
		this.count += increment;
	}
	
	public void setCount(int count) {
		this.count = count;
	}

	public int getCount() {
		return count;
	}
	public void setCoverage(double coverage) {
		this.coverage = coverage;
	}

	public double getCoverage() {
		return coverage;
	}
	
	

}
