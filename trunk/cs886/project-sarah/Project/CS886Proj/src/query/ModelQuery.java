/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package query;

import BayesianInferences.BucketTree;
import BayesianNetworks.ProbabilityFunction;
import InferenceGraphs.InferenceGraph;
import InferenceGraphs.InferenceGraphNode;
import InterchangeFormat.IFException;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author snadi
 */
public class ModelQuery {

    private String modelFileName;
    private InferenceGraph inferenceGraph;

    public ModelQuery(String modelFileName) {
        try {
            this.modelFileName = modelFileName;
            inferenceGraph = new InferenceGraph(modelFileName);
        } catch (IOException ex) {
            Logger.getLogger(ModelQuery.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IFException ex) {
            Logger.getLogger(ModelQuery.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void test(String name){
        inferenceGraph.get_node(name);
    }

    public ProbabilityFunction query(Vector<String> observedVariables, String queriedVariable) {
        
        for (String node : observedVariables) {
            InferenceGraphNode graphNode = inferenceGraph.get_node(node);
            if(graphNode != null)
                inferenceGraph.get_node(node).set_observation_value("true");
            else{
                return null;
            }

        }        
        
        ProbabilityFunction pfunction = inferenceGraph.performInference(queriedVariable, false, false);
        inferenceGraph.reset_expectation();
        inferenceGraph.reset_marginal();
        return pfunction;        
    }

    public Vector<InferenceGraphNode> getAllVariables() {
        return inferenceGraph.get_nodes();
    }
}
