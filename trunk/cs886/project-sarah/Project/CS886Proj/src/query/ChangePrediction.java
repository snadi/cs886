/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package query;

import data.ChangedCI;
import java.util.Vector;

/**
 *
 * @author nadsa02
 */
public class ChangePrediction {

    private String changeRefNum;
    private String initialEntity;
    private Vector<String> initialCIs;
    private Vector<ChangedCI> occurredSet;
    private Vector<ChangedCI> predictedSet;
    private Vector<String> intersectionSet;
    private double recall;
    private double precision;
    private int missingCIs;
    private static int countCase1 = 0;
    private static int countCase2 = 0;
    private static int countCase3 = 0;
    private static int countCase4 = 0;

    public ChangePrediction() {
        occurredSet = new Vector<ChangedCI>();
        predictedSet = new Vector<ChangedCI>();
        intersectionSet = new Vector<String>();
        initialCIs = new Vector<String>();
        recall = 0;
        precision = 0;
        missingCIs = 0;
    }


    public Vector<String> getInitialCIs() {
        return initialCIs;
    }

    public void setInitialCIs(Vector<String> initialCIs) {
        this.initialCIs = initialCIs;
    }

    public double getPrecision() {
        return precision;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    public double getRecall() {
        return recall;
    }

    public void setRecall(double recall) {
        this.recall = recall;
    }

    public String getInitialEntity() {
        return initialEntity;
    }

    public void setInitialEntity(String initialEntity) {
        this.initialEntity = initialEntity;
    }

    public String getChangeRefNum() {
        return changeRefNum;
    }

    public void setChangeRefNum(String changeRefNum) {
        this.changeRefNum = changeRefNum;
    }

    public Vector<String> getIntersectionSet() {
        return intersectionSet;
    }

    public void setIntersectionSet(Vector<String> intersectionSet) {
        this.intersectionSet = intersectionSet;
    }

    public Vector<ChangedCI> getOccurredSet() {
        return occurredSet;
    }

    public void setOccurredSet(Vector<ChangedCI> occurredSet) {
        this.occurredSet = occurredSet;
    }

    public Vector<ChangedCI> getPredictedSet() {
        return predictedSet;
    }

    public void setPredictedSet(Vector<ChangedCI> predictedSet) {
        this.predictedSet = predictedSet;
    }

    public int getMissingCIs() {
        return missingCIs;
    }

    public void setMissingCIs(int missingCIs) {
        this.missingCIs = missingCIs;
    }

    public void calculateIntersection() {
       // System.out.println("-----------------");
        //System.out.println("occurred set size: " + occurredSet.size());
        //System.out.println("predicted set size: " + predictedSet.size());

        //check first that both sets are not empty to avoid unnecessary looping
        //if either sets are empty, then the intersection is empty
        if (predictedSet.size() != 0 && occurredSet.size() != 0) {
            for (int i = 0; i < predictedSet.size(); i++) {
                if (occurredSet.contains(predictedSet.get(i))) {
                    intersectionSet.add(predictedSet.get(i).getCiID());
                }
            }
        }

        //System.out.println("intersection set size: " + intersectionSet.size());
        
        if (occurredSet.size() + missingCIs == 0 && predictedSet.size() == 0) {
            //both occurred and prediction are empty
            countCase1++;
            recall = 1;
            precision = 1;
        } else if (occurredSet.size() + missingCIs == 0) {
            //only occurred is empty
            countCase2++;
            recall = 1;
            precision = 0;
        } else if (predictedSet.size() == 0) {
            //only predicted is empty
            countCase3++;
            recall = 0;
            precision = 1;
        } else {
            //both are not empty
            countCase4++;
            recall = (double) intersectionSet.size() / ((double) occurredSet.size() + missingCIs);
            precision = (double) intersectionSet.size() / (double) predictedSet.size();
        }     
    }

  
}
