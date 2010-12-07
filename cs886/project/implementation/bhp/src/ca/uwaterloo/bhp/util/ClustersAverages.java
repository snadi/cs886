package ca.uwaterloo.bhp.util;

import ca.uwaterloo.bhp.feature.FeatureName;

public class ClustersAverages {

	public static double getColdPathAverage(FeatureName featureName) {
		switch (featureName) {
			case ASSIGNMENTS:
				return 49.2574;
			case DEREFERNCES:
				return 15.021675;
			case EQUAL:
				return 5.003275;
			case FIELDS:
				return 11.35565;
			case FIELDS_WRITTEN:
				return 2.360325;
			case GOTO:
				return 12.299325;
			case IF:
				return 10.394675;
			case INVOCATIONS:
				return 32.473975;
			case LOCALS:
				return 127.781625;
			case NEW:
				return 5.728125;
			case STATEMENTS:
				return 77.204025;
			case THROWS:
				return 0.0881;
			default:
				return 0;
		}
	}
	
	public static double getHotPathAverage(FeatureName featureName) {
		switch (featureName) {
			case ASSIGNMENTS:
				return 15.268575;
			case DEREFERNCES:
				return 5.959225;
			case EQUAL:
				return 1.218475;
			case FIELDS:
				return 3.67495;
			case FIELDS_WRITTEN:
				return 0.59825;
			case GOTO:
				return 4.3059;
			case IF:
				return 3.50735;
			case INVOCATIONS:
				return 9.767475;
			case LOCALS:
				return 40.898725;
			case NEW:
				return 1.341925;
			case STATEMENTS:
				return 25.953825;
			case THROWS:
				return 0.03515;
			default:
				return 0;
		}
	}
}