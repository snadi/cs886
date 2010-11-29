/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import norsys.netica.Environ;
import norsys.netica.Net;
import norsys.netica.NeticaException;
import norsys.netica.Node;
import norsys.netica.NodeList;
import norsys.netica.Streamer;
import utilities.Utilities;

/**
 *
 * @author snadi
 */
public class LearnStructure {

    private Environ env;
    private Net net;

    public LearnStructure() {
        try {
            env = new Environ(null);
            net = new Net();
            net.setName("ChangeSets");
        } catch (NeticaException ex) {
            Logger.getLogger(LearnStructure.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String args[]) {
        LearnStructure learnStructure = new LearnStructure();
        learnStructure.addNodes();
        learnStructure.addLinks();
    }

    private void addNodes() {

        try {
            File nodeFile = new File("nodes.txt");
            BufferedReader fileReader = new BufferedReader(new FileReader(nodeFile));
            String line = fileReader.readLine();
            String[] parts = line.split(" ");
            String nodeValue = parts[0];
            String nodeTitle = parts[1];

            while (nodeValue != null) {
                Node node = new Node(nodeValue, "true,false", net);
                node.setTitle(nodeTitle);
                line = fileReader.readLine();
                parts = line.split(" ");
                nodeValue = parts[0];
                nodeTitle = parts[1];
            }

        } catch (IOException ex) {
            Logger.getLogger(LearnStructure.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(LearnStructure.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void addLinks() {
        try {


            NodeList nodeList = net.getNodes();
            for (int i = 0; i < nodeList.size(); i++) {
                Node node = nodeList.getNode(i);

                String query = "Select hier_parent, parent.own_Resource_uuid as parentUuid, child.own_resource_uuid as childUuid, "
                        + "hier_child,  ci_rel_type.parenttochild from \n"
                        + "ca_owned_resource as child, ca_owned_resource as parent, busmgt where \n"
                        + "busmgt.hier_parent = parent.own_resource_uuid and \n"
                        + "busmgt.hier_child = child.own_resource_uuid and \n"
                        + "busmgt.ci_rel_type = ci_rel_type.id and \n "
                        + "parent.own_resource_uuid = '" + node
                        + "' or child.own_resource_uuid = '" + node + "') "
                        + "and busmgt.del = 0";

                ResultSet rs = Utilities.executeQuery(Utilities.getLocalConnection(), query);

                while (rs.next()) {

                    String parentUuid = rs.getString("parentUuid");
                    String childUuid = rs.getString("childUuid");

                    Node parentNode = net.getNode(parentUuid);
                    Node childNode = net.getNode(childUuid);

                    if (parentNode != null && childNode != null) {
                        childNode.addLink(parentNode);
                    }
                }
            }

            Streamer stream = new Streamer("ChangeSets.dne");
            net.write(stream);
            net.finalize();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
