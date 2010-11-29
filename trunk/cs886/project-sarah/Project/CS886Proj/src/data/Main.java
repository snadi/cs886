/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import query.ChangePredictor;

/**
 *
 * @author snadi
 */
public class Main {

    public static void main(String args[]) {
        PrintWriter filePrinter = null;
        try {

//                  ModelQuery modelQuery  = new ModelQuery("mygraph.xml");
//                 Vector<String> observations = new Vector<String>();
//                  observations.add("X3FB8368D482301DA96D10014220897B8");
//                  observations.add("X40096F74482301DAB4E00014220897B8");
//                  modelQuery.test("X4026C061482301DAA1F00014220897B8");
            //
            //      ChangeSetDetector detector = new ChangeSetDetector("X3FB8368D482301DA96D10014220897B8", "mygraph.xml");
            //      printSet(detector.findChangeSet());
            //
            double threshold = 0;
             DataCollector dataCollector = new DataCollector(1207008000, 1209600000);
             Hashtable<String, Vector<ChangedCI>> testingData =dataCollector.collectData(false);
            while (threshold <= 1) {
                ChangePredictor testChangePredictor = new ChangePredictor(testingData, "data/Jan08-March08-2pars/graph.xml");
                testChangePredictor.resetCounts();
                System.out.println("threshold: " + threshold);
                File resourceFile = new File("data/Jan08-March08-2pars/output_" + threshold+ ".txt");
                filePrinter = new PrintWriter(resourceFile);               
                testChangePredictor.calculatePredictions(threshold);
                testChangePredictor.printResults(filePrinter);
                System.out.println(" Overall recall " + testChangePredictor.getOverallRecall());
                System.out.println(" Overall precision " + testChangePredictor.getOverallPrecision());
                threshold += 0.1;
                System.out.println("finished file");
                filePrinter.close();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            filePrinter.close();
        }


    }

    public static void printSet(Vector<ChangedCI> set) {
        for (ChangedCI ci : set) {
            System.out.println(ci.getCiID() + "  ,  " + ci.getProbability());
        }
    }
}
