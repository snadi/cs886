package ca.uwaterloo.bhp.weka;

import java.io.IOException;
import java.util.Collection;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import ca.uwaterloo.bhp.cfg.ExecutionPath;

public class ArffWriter extends ArffSaver {

	private static final long serialVersionUID = 1251197729530350333L;
	
	private Collection<ExecutionPath> executionPaths;
	private Instances instances;

	public ArffWriter(String dir, String className, String methodName, Collection<ExecutionPath> executionPaths) {
		super();
		setFileExtension(Instances.FILE_EXTENSION);
		setDir(dir);
		setFilePrefix(className);
		setDirAndPrefix(methodName, "");
		this.executionPaths = executionPaths;
		
		// Create the instances based on the execution paths
		createInstances();
	}
	
	private void createInstances() {
		String relationName = retrieveFile().getName();
		
		int capacity = executionPaths.size();
		
		instances = new Instances(relationName, attInfo, capacity);
	}
	
	public void write() throws IOException {
		writeBatch();
	}

}
