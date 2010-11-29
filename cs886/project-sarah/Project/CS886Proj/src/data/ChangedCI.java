/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package data;

/**
 *
 * @author snadi
 */
public class ChangedCI implements Comparable{

    private String ciID;
    private double probability;

    public ChangedCI(String ciID, double probability) {
        this.ciID = ciID;
        this.probability = probability;
    }

    public ChangedCI(String ciID) {
        this.ciID = ciID;
        this.probability = probability;
    }

    public String getCiID() {
        return ciID;
    }

    public double getProbability() {
        return probability;
    }

    public void setCiID(String ciID) {
        this.ciID = ciID;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    @Override
    public boolean equals(Object o) {        
        if(o instanceof  ChangedCI){
            return ((ChangedCI) o).ciID.equals(ciID);
        }else if(o instanceof String){
            return ((String) o).equals(ciID);
        }

        return false;
    }

    public int compareTo(Object o) {
        System.out.println("comare to");
        if(o instanceof  ChangedCI){
            if(((ChangedCI) o).ciID.equals(ciID)){
                return 0;
            }else
                return -1;
        }else if(o instanceof String){
            if(((String) o).equals(ciID))
                    return 0;
            else
                return -1;
        }

        return -1;
    }


    

}
