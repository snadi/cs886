/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import utilities.Utilities;

/**
 *
 * @author snadi
 */
public class LearnStructure {

    private Vector<Node> nodes;
    private Vector<String> names;
    private Vector<String> addedCIs;

    public LearnStructure() {
        nodes = new Vector<Node>();
        names = new Vector<String>();
        addedCIs = new Vector<String>();
    }

    public static void main(String args[]) {
        LearnStructure learnStructure = new LearnStructure();
        learnStructure.addNodes();
        learnStructure.addLinks();
        learnStructure.writeToFile("sarah.xml");
    }

     public String getBIFHeader() {
    StringBuffer text = new StringBuffer();
    text.append("<?xml version=\"1.0\"?>\n");
    text.append("<!-- DTD for the XMLBIF 0.3 format -->\n");
    text.append("<!DOCTYPE BIF [\n");
    text.append("	<!ELEMENT BIF ( NETWORK )*>\n");
    text.append("	      <!ATTLIST BIF VERSION CDATA #REQUIRED>\n");
    text.append("	<!ELEMENT NETWORK ( NAME, ( PROPERTY | VARIABLE | DEFINITION )* )>\n");
    text.append("	<!ELEMENT NAME (#PCDATA)>\n");
    text.append("	<!ELEMENT VARIABLE ( NAME, ( OUTCOME |  PROPERTY )* ) >\n");
    text.append("	      <!ATTLIST VARIABLE TYPE (nature|decision|utility) \"nature\">\n");
    text.append("	<!ELEMENT OUTCOME (#PCDATA)>\n");
    text.append("	<!ELEMENT DEFINITION ( FOR | GIVEN | TABLE | PROPERTY )* >\n");
    text.append("	<!ELEMENT FOR (#PCDATA)>\n");
    text.append("	<!ELEMENT GIVEN (#PCDATA)>\n");
    text.append("	<!ELEMENT TABLE (#PCDATA)>\n");
    text.append("	<!ELEMENT PROPERTY (#PCDATA)>\n");
    text.append("]>\n");
    return text.toString();
  } // getBIFHeader

    private void writeToFile(String fileName) {
        PrintWriter printWriter = null;

        try {
            File file = new File(fileName);
            printWriter = new PrintWriter(file);
            printWriter.println(getBIFHeader());
            printWriter.println("<BIF VERSION=\"0.3\">");
            printWriter.println("<NETWORK>");
            printWriter.println("<NAME>observations</NAME>");
            
            for(int i=0; i < names.size(); i++){
                printWriter.println("<VARIABLE TYPE=\"nature\">");
                printWriter.println("<NAME>" + names.get(i) + "</NAME>");
                printWriter.println("<OUTCOME>true</OUTCOME>");
                printWriter.println("<OUTCOME>false</OUTCOME>");
                printWriter.println("<PROPERTY>position = (0,0)</PROPERTY>");
                printWriter.println("</VARIABLE>");
            }

            for (int i = 0; i < nodes.size(); i++) {
                printWriter.println("<DEFINITION>");
                printWriter.println("<FOR>" + nodes.get(i).getNodeName() + "</FOR>");
                for(int j=0; j < nodes.get(i).getParents().size(); j++){
                    printWriter.println("<GIVEN>" + nodes.get(i).getParents().get(j) + "</GIVEN>");
                }
                printWriter.println("</DEFINITION>");
            }   

            printWriter.println("</NETWORK>");
            printWriter.println("</BIF>");

        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenerateObservations.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            printWriter.close();
        }
    }

    private void addNodes() {

        try {
            File nodeFile = new File("nodes.txt");
            BufferedReader fileReader = new BufferedReader(new FileReader(nodeFile));
            String nodeName = fileReader.readLine();

            while (nodeName != null) {
                Node node = new Node(nodeName);
                nodes.add(node);
                names.add(nodeName);
                nodeName = fileReader.readLine();
            }

        } catch (IOException ex) {
            Logger.getLogger(LearnStructure.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(LearnStructure.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void addLinks() {
        try {


            for (int i = 0; i < nodes.size(); i++) {
                Node node = nodes.get(i);

                String query = "Select hier_parent, parent.own_Resource_uuid as parentUuid, child.own_resource_uuid as childUuid, "
                        + "hier_child from \n"
                        + "ca_owned_resource as child, ca_owned_resource as parent, ci_rel_type, busmgt where \n"
                        + "busmgt.hier_parent = parent.own_resource_uuid and \n"
                        + "busmgt.hier_child = child.own_resource_uuid and \n"
                        + "busmgt.ci_rel_type = ci_rel_type.id and \n "
                        + "child.own_resource_uuid = '" + node.getNodeName().substring(1, node.getNodeName().length()) + "' ";
                        //   + "' or child.own_resource_uuid = '" + node.getNodeName() + "') "
                        

                ResultSet rs = Utilities.executeQuery(Utilities.getLocalConnection(), query);

                while (rs.next()) {

                    String parentUuid = rs.getString("parentUuid");
                    String childUuid = rs.getString("childUuid");

                    //if (nodes.contains(parentUuid) && nodes.contains(childUuid)) {
                      //  System.out.println("added parent");
                        node.addParent(parentUuid);
                        addedCIs.add(parentUuid);
                    //}
                }
            }

            System.out.println(addedCIs.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
