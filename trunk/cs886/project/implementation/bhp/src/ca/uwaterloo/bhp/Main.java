package ca.uwaterloo.bhp;

import java.io.IOException;

import ca.uwaterloo.bhp.preprocessing.DataPreprocessor;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			DataPreprocessor.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
