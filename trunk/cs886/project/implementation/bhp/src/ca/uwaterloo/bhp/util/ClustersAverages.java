package ca.uwaterloo.bhp.util;

import ca.uwaterloo.bhp.feature.FeatureName;

public class ClustersAverages {
	public static int TEN = 0;
	public static int TWENTY = 1;
	public static int THIRTY = 2;
	public static int FOURTY = 3;
	public static int FIFTY = 4;
	
	public static int TRAINING = 0;
	public static int TEST = 1;
	
	public static double values[][] = new double[][] {
		{16.6515, 5.2987, 2.6515, 2.4957, 0.632, 5.842, 4.8377, 11.1861, 46.0779, 1.7056, 29.461, 0.013},
		{16.7863, 5.4038, 2.6368, 2.5705, 0.6667, 5.7158, 4.7821, 11.2885, 46.0919, 1.797, 29.5812, 0.0128}
	};
	
	public static double[][] avg = new double[][] {
		{3.714, 2.9033, 0.142, 1.1788, 0.4753, 0.5498, 0.4308, 2.3236, 10.4997, 0.4252, 7.9081, 0.0334},
		{16.9907, 4.0865, 1.9292, 2.0805, 0.2735, 7.7237, 6.5747, 12.4984, 45.1786, 0.8422, 30.7885, 0.025},
		{23.1365, 6.5968, 2.5739, 4.2011, 0.6598, 8.4264, 7.0163, 16.3752, 58.4237, 1.7663, 38.9947, 0.0556},
		{69.3282, 12.4859, 5.554, 9.7243, 0.7413, 13.8151, 10.756, 74.8947, 194.84, 8.1021, 120.8035, 0.0238},
		{56.5917, 13.8164, 13.8748, 10.5063, 2.4874, 23.5838, 19.88, 40.7381, 132.9246, 2.6921, 88.2042, 0.0088}
	};
		
	public static double getHotPathAverage(FeatureName featureName, int threshold) {
		int thresholdIndex = (threshold / 10) - 1;
		switch (featureName) {
			case ASSIGNMENTS:
				return avg[thresholdIndex][0];
			case DEREFERNCES:
				return avg[thresholdIndex][1];
			case EQUAL:
				return avg[thresholdIndex][2];
			case FIELDS:
				return avg[thresholdIndex][3];
			case FIELDS_WRITTEN:
				return avg[thresholdIndex][4];
			case GOTO:
				return avg[thresholdIndex][5];
			case IF:
				return avg[thresholdIndex][6];
			case INVOCATIONS:
				return avg[thresholdIndex][7];
			case LOCALS:
				return avg[thresholdIndex][8];
			case NEW:
				return avg[thresholdIndex][9];
			case STATEMENTS:
				return avg[thresholdIndex][10];
			case THROWS:
				return avg[thresholdIndex][11];
			default:
				return 0;
		}
	}
}