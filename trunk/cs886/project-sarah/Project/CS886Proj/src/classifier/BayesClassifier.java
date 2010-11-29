/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package classifier;

import java.io.File;
import java.io.FileWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.estimate.SimpleEstimator;
import weka.classifiers.bayes.net.search.local.K2;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;

/**
 *
 * @author snadi
 */
public class BayesClassifier {

    private String trainingData;
    private String testingData;

    public BayesClassifier(String trainingData) {
        this.trainingData = trainingData;
    }

    public BayesClassifier(String trainingData, String testingData) {
        this.trainingData = trainingData;
        this.testingData = testingData;
    }

    public void classify() {
        try {
            BayesNet bayesNet = new BayesNet();
            DataSource source = new DataSource(trainingData);
            Instances instances = source.getDataSet();
            // Make the last attribute be the class
            instances.setClassIndex(instances.numAttributes() - 1);

            //bayesNet.setOptions(Utils.splitOptions(" -D -Q weka.classifiers.bayes.net.search.local.K2 -- -P 5 -S BAYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5 "));
            bayesNet.setUseADTree(false);
            K2 search = new K2();
            search.setMaxNrOfParents(5);
            bayesNet.setSearchAlgorithm(search);
            SimpleEstimator estimator = new SimpleEstimator();
            estimator.setAlpha(0.5);
            bayesNet.setEstimator(estimator);
            
             bayesNet.buildClassifier(instances);

            FileWriter fileWriter = new FileWriter(new File("output.xml"));
            fileWriter.write(bayesNet.toXMLBIF03());
            fileWriter.close();
        } catch (Exception ex) {
            Logger.getLogger(BayesClassifier.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private String[] getOptions() {
        String options[] = new String[4];
        options[0] = "-D";
        options[1] = "-Q weka.classifiers.bayes.net.search.local.K2 -- -P 5";
        options[2] = "-S BAYES -E weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5";
        return options;
    }
}
