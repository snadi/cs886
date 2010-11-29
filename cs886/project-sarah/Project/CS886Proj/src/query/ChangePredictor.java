/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package query;

import BayesianNetworks.ProbabilityFunction;
import InferenceGraphs.InferenceGraphNode;
import data.ChangedCI;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import utilities.Utilities;

/**
 *
 * @author nadsa02
 */
public class ChangePredictor {

    private Vector<ChangePrediction> changePredictions;
    private Hashtable<String, Vector<ChangedCI>> testSet;
    private float overallRecall;
    private float overallPrecision;
    private static int predictionSizeSum = 0;
    private static int numOfPredictions = 0;
    private String graphFile;

    public ChangePredictor(Hashtable<String, Vector<ChangedCI>> testSet, String graphFile) {
        changePredictions = new Vector<ChangePrediction>();
        this.testSet = testSet;
        this.graphFile = graphFile;
    }

    public void resetCounts(){
        predictionSizeSum = 0;
        numOfPredictions = 0;
    }

    private void calculateOverallRecallPrecision() {
        float recallSum = 0;
        float precisionSum = 0;

        for (int i = 0; i < changePredictions.size(); i++) {
            ChangePrediction changePrediction = changePredictions.get(i);

            //changeLearner.rankPredictedSet(changePrediction.getPredictedSet());
            changePrediction.calculateIntersection();
            predictionSizeSum += changePrediction.getPredictedSet().size();
            numOfPredictions++;


            recallSum += changePrediction.getRecall();
            precisionSum += changePrediction.getPrecision();
        }

        overallRecall = recallSum / (float) changePredictions.size();
        overallPrecision = precisionSum / (float) changePredictions.size();
    }

    public static double getAvgPredSize() {
        return ((double) predictionSizeSum) / ((double) numOfPredictions);
    }

    public void printResults(PrintWriter writer) {
        System.out.println("printin in file");
        writer.println("recall: "+ overallRecall + ", precision: " + overallPrecision);
    }

    public void printResults(String fileName) {
        PrintWriter filePrinter = null;
        try {
            float recallSum = 0;
            float precisionSum = 0;

            File resourceFile = new File(fileName);
            filePrinter = new PrintWriter(resourceFile);
            filePrinter.println("ref num, occurred set, initial CI, prediction Set, intersction set, recall, precision");
            for (int i = 0; i < changePredictions.size(); i++) {
                ChangePrediction changePrediction = changePredictions.get(i);
                filePrinter.println(changePrediction.getChangeRefNum() + ","
                        + Utilities.getString(changePrediction.getOccurredSet()) + ","
                        + changePrediction.getInitialEntity() + ","
                        + Utilities.getString(changePrediction.getPredictedSet()) + ","
                        + Utilities.getString(changePrediction.getIntersectionSet()) + ","
                        + changePrediction.getRecall() + ","
                        + changePrediction.getPrecision());
                recallSum += changePrediction.getRecall();
                precisionSum += changePrediction.getPrecision();
            }

            //  System.out.println("Overall recall " + overallRecall);
            //  System.out.println("Overall precision " + overallPrecision);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } finally {
            filePrinter.close();

        }
    }

    public void printResults(int month) {
        float recallSum = 0;
        float precisionSum = 0;

        for (int i = 0; i < changePredictions.size(); i++) {
            ChangePrediction changePrediction = changePredictions.get(i);
            recallSum += changePrediction.getRecall();
            precisionSum += changePrediction.getPrecision();
        }

    }

    public void calculatePredictions(double threshold) {


        ModelQuery modelQuery = new ModelQuery(graphFile);
        int counter = 1;

        for (Enumeration e = testSet.keys(); e.hasMoreElements();) {

            String key = (String) e.nextElement();
            Vector<ChangedCI> changeSet = new Vector<ChangedCI>(testSet.get(key));            

            ChangePrediction changePrediction = new ChangePrediction();
            changePrediction.setChangeRefNum(key);


            ChangedCI initialResource = changeSet.get(0);
            changePrediction.setInitialEntity(initialResource.getCiID());

            //set the occurred set as changeSet - initialEntity
            changeSet.remove(initialResource);
            changePrediction.setOccurredSet(changeSet);

            //set the initial entity as the chosen one
            Vector<String> initialCIs = new Vector<String>();
            initialCIs.add(initialResource.getCiID());
            Vector<String> confirmedCIs = new Vector<String>(initialCIs);
            Vector<ChangedCI> newPredictedSet = new Vector<ChangedCI>();

            int level = 1;
            boolean loopAgain = false;
            boolean addPrediction = false;
            do {
                loopAgain = false;
                Vector<InferenceGraphNode> graphNodes = modelQuery.getAllVariables();
                newPredictedSet = new Vector<ChangedCI>();

                double posterior = 0;
                int countNodes = 1;

                for (InferenceGraphNode node : graphNodes) {

                    ProbabilityFunction probFunction = modelQuery.query(confirmedCIs, node.get_name());

                    if (probFunction == null) {
                        //initial node is not in graph
                        posterior = 0;
                        break;
                    } else {
                        posterior = probFunction.get_value(0);
                        addPrediction = true;
                    }


                    if (posterior > threshold && !changePrediction.getPredictedSet().contains(new ChangedCI(node.get_name()))) {
                        newPredictedSet.add(new ChangedCI(node.get_name(), posterior));
                    }
                }

                for (int i = 0; i < newPredictedSet.size(); i++) {
                    if (changePrediction.getOccurredSet().contains(newPredictedSet.get(i))) {
                        confirmedCIs.add(newPredictedSet.get(i).getCiID());
                        loopAgain = true;
                    }
                }

                changePrediction.getPredictedSet().addAll(newPredictedSet);
                level++;

            } while (loopAgain);

            if (addPrediction) {
                changePredictions.add(changePrediction);
            }
        }

        System.out.println("tested: " + changePredictions.size());
        calculateOverallRecallPrecision();


    }

    public Vector<ChangePrediction> getChangePredictions() {
        return changePredictions;
    }

    public void setChangePredictions(Vector<ChangePrediction> changePredictions) {
        this.changePredictions = changePredictions;
    }

    public Hashtable<String, Vector<ChangedCI>> getTestSet() {
        return testSet;
    }

    public void setTestSet(Hashtable<String, Vector<ChangedCI>> testSet) {
        this.testSet = testSet;
    }

    public float getOverallPrecision() {
        return overallPrecision;
    }

    public void setOverallPrecision(float overallPrecision) {
        this.overallPrecision = overallPrecision;
    }

    public float getOverallRecall() {
        return overallRecall;
    }

    public void setOverallRecall(float overallRecall) {
        this.overallRecall = overallRecall;
    }
}
