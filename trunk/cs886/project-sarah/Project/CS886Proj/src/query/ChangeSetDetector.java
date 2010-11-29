/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package query;

import BayesianNetworks.ProbabilityFunction;
import InferenceGraphs.InferenceGraphNode;
import data.ChangedCI;
import java.util.Vector;

/**
 *
 * @author snadi
 */
public class ChangeSetDetector {

    private String initialCI;
    private Vector<ChangedCI> changeSet;
    private ModelQuery modelQuery;

    public ChangeSetDetector(String initialCI, String fileName){
        this.initialCI = initialCI;
        modelQuery = new ModelQuery(fileName);
        changeSet = new Vector<ChangedCI>();
    }

    public Vector<ChangedCI> findChangeSet(){
        Vector<String> observedVariables = new Vector<String>();
        observedVariables.add(initialCI);


//        Vector<InferenceGraphNode> graphNodes = modelQuery.getAllVariables();
//
//        double posterior = 0;
//        for(InferenceGraphNode node : graphNodes){
//            ProbabilityFunction probFunction = modelQuery.query(observedVariables, node.get_name());
//            posterior = probFunction.get_value(0);
//            if(posterior > 0.1 && !changeSet.contains(node.get_name())){
//                changeSet.add(new ChangedCI(node.get_name(), posterior));
//                observedVariables.add(node.get_name());
//            }
//        }

        return changeSet;
    }

}
