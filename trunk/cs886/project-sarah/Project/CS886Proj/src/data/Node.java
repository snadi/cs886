package data;


import java.util.Vector;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author snadi
 */
public class Node {

    private String nodeName;
    private String type;
    private Vector<String> values;
    private Vector<String> parents;

    public Node(String nodeName) {
        this.nodeName = nodeName;
        parents = new Vector<String>();
    }

    public void addParent(String parent){

    }
    public String getNodeName() {
        return nodeName;
    }

    public Vector<String> getParents() {
        return parents;
    }

    public String getType() {
        return type;
    }

    public Vector<String> getValues() {
        return values;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public void setParents(Vector<String> parents) {
        this.parents = parents;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValues(Vector<String> values) {
        this.values = values;
    }


}
