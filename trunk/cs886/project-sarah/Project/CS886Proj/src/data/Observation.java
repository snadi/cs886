/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package data;

/**
 *
 * @author snadi
 */
public class Observation {

    private String variableId;
    private boolean state;

    public Observation(String variableId, boolean state) {
        this.variableId = variableId;
        this.state = state;
    }

    public boolean isState() {
        return state;
    }

    public String getVariableId() {
        return variableId;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public void setVariableId(String variableId) {
        this.variableId = variableId;
    }

    

}
