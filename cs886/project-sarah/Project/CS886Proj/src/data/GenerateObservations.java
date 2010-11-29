/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import utilities.Utilities;
import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.TreeBidiMap;

/**
 *
 * @author snadi
 */
public class GenerateObservations {

    private Connection connection;
    private BidiMap indexToID;
    private int[][] graph;
    private Vector<String> cis;
    private int startDate;
    private int endDate;
    private String[][] observations;
    private Hashtable<String, Vector<String>> reportSet;
    private Hashtable<String, Integer> ciCount;
    private Vector<Integer> removedCIs;
    private String[] alphabet = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    public static void main(String args[]) {
        GenerateObservations generateObservations = new GenerateObservations(1199145600, 1206921600);
        generateObservations.displayCounts();
        generateObservations.writeCis();
    }

    public GenerateObservations(int startDate, int endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        initialize();

    }

    public void displayCounts() {
        int count_one = 0;
        int count_two = 0;
        int count_more = 0;
        for (Object key : ciCount.keySet()) {
            int count = ciCount.get(key);
            if (count == 1) {
                count_one++;
            } else if (count == 2) {
                count_two++;
            } else if (count >= 10) {
                count_more++;
            }
        }

        System.out.println(count_one + "cis appearing once");
        System.out.println(count_two + "cis appearing twice");
        System.out.println(count_more + "cis appearing more than ten times");
    }

    private void initialize() {
        try {
            System.out.println("initialized enterd");
            connection = Utilities.getLocalConnection();
            indexToID = new TreeBidiMap();
            graph = new int[indexToID.size()][indexToID.size()];
            cis = new Vector<String>();
            ciCount = new Hashtable<String, Integer>();
            removedCIs = new Vector<Integer>();
            //fillIDMap();
            getObservations();
            preprocess();
            generateMatrix();
            connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(GenerateObservations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void preprocess() {
        int removedCount = 0;
        for (Object key : ciCount.keySet()) {
            int count = ciCount.get(key);
            if (count <= 9) {
                cis.remove(key);
                removedCount++;
            }
        }

        System.out.println("removed: " + removedCount);
    }

    private void fillIDMap() {
        try {
            String query = "Select own_resource_uuid from ca_owned_resource order by own_resource_uuid";
            ResultSet resultSet = Utilities.executeQuery(connection, query);
            int count = 0;

            while (resultSet.next()) {
                indexToID.put(count, resultSet.getString("own_resource_uuid"));
                count++;
            }
            System.out.println("Found: " + count + " CIs.");
        } catch (SQLException ex) {
            Logger.getLogger(GenerateObservations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void getObservations() {
        try {
            ResultSet resultSet = null;
            reportSet = new Hashtable<String, Vector<String>>();
            String query = "Select open_date, chg.chg_ref_num, own_resource_uuid "
                    + "from lrel join chg on "
                    + "lrel.l_persid = chg.persid "
                    + "join ca_owned_resource on "
                    + "lrel.r_persid = concat('nr:',own_resource_uuid) "
                    + "where open_date >= " + startDate + " and open_date <= " + endDate + " "
                    + "group by chg_ref_num, own_resource_uuid, open_Date  "
                    + "order by chg_ref_num, open_date, own_Resource_uuid";
            System.out.println("starting query");
            resultSet = Utilities.executeQuery(connection, query);
            System.out.println("executed query");
            while (resultSet.next()) {
                //  System.out.println("------------------------------------------");
                String changedCI = "X" + resultSet.getString("own_resource_uuid");
                Vector<String> vector = reportSet.get(resultSet.getString("chg_ref_num"));
                if (vector == null) {
                    vector = new Vector<String>();
                }
                if (!vector.contains(changedCI)) {
                    vector.add(changedCI);
                }
                reportSet.put(resultSet.getString("chg_ref_num"), vector);

                if (!cis.contains(changedCI) && cis.indexOf(changedCI) < 0) {
                    //    System.out.println("adding changedCI");
                    cis.add(changedCI);
                }

                if (ciCount.containsKey(changedCI)) {
                    ciCount.put(changedCI, ciCount.get(changedCI) + 1);
                } else {
                    ciCount.put(changedCI, 1);
                }

            }

            System.out.println(cis.size() + " distinct cis");

        } catch (SQLException ex) {
            Logger.getLogger(GenerateObservations.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void generateMatrix() {
        observations = new String[reportSet.size()][cis.size()];

        for (int i = 0; i < reportSet.size(); i++) {
            for (int j = 0; j < cis.size(); j++) {
                observations[i][j] = "false";
            }
        }

        int observationIndex = 0;

        for (Enumeration e = reportSet.keys(); e.hasMoreElements();) {

            String key = (String) e.nextElement();
            Vector<String> affectedResources = reportSet.get(key);

            for (int i = 0; i < affectedResources.size(); i++) {
                String affectedResource = affectedResources.get(i);
                if (cis.contains(affectedResource)) {
                    observations[observationIndex][cis.indexOf(affectedResource)] = "true";
                }
            }

            observationIndex++;
        }

        writeToFile();

    }

    private String convertToString(Vector<String> changeSet) {
        String result = "";
        for (String ci : changeSet) {
            result += ci + "    ";
        }

        return result;
    }

    private void writeToFile() {
        PrintWriter printWriter = null;

        try {
            File file = new File("observations.arff");
            printWriter = new PrintWriter(file);

            //header
            printWriter.println("@relation observations");

            //variables
            for (int i = 0; i < cis.size(); i++) {
                printWriter.println("@attribute " + cis.get(i) + " {true,false}");
            }
            int count = 0;
            int countNotWriten = 0;
            //data
            printWriter.println("@data");
            for (int i = 0; i < reportSet.size(); i++) {
                String observation = "";
                boolean flag = false;
                for (int j = 0; j < cis.size(); j++) {
                    if (observations[i][j].equals("true")) {
                        flag = true;
                    }
                    observation += observations[i][j] + ",";
                    observation = observation.substring(0, observation.length());
                }
                if (flag) {
                    printWriter.println(observation);
                    count++;
                } else {
                    countNotWriten++;
                }
            }

            System.out.println(count + " observations");
            System.out.println(countNotWriten + " not written");

        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenerateObservations.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            printWriter.close();
        }
    }

    private void writeCis() {
        PrintWriter printWriter = null;

        try {
            File file = new File("nodes.txt");
            printWriter = new PrintWriter(file);

            for (int i = 0; i < cis.size(); i++) {
                 printWriter.println( cis.get(i));
               //printWriter.println(generateRandomLabel(i) + "   " + cis.get(i).substring(1, cis.get(i).length()));
            }

            System.out.println("Wrote: " + cis.size());

        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenerateObservations.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            printWriter.close();
        }
    }

    private String generateRandomLabel(int i) {

        if (i < 26) {
            return alphabet[i];
        } else if (i < 26 * 2) {
            return alphabet[25 - i % 26] + alphabet[25 - i % 26];
        } else if (i < 26 * 2) {
            return alphabet[25 - i % 26] + alphabet[25 - i % 26] + alphabet[25 - i % 26];
        } else if (i < 26 * 3) {
            return alphabet[25 - i % 26] + alphabet[25 - i % 26] + alphabet[25 - i % 26] + alphabet[25 - i % 26];
        } else if (i < 26 * 4) {
            return alphabet[25 - i % 26] + alphabet[25 - i % 26] + alphabet[25 - i % 26] + alphabet[25 - i % 26] + alphabet[25 - i % 26];
        } else if (i < 26 * 5) {
            return alphabet[25 - i % 26] + alphabet[25 - i % 26] + alphabet[25 - i % 26] + alphabet[25 - i % 26] + alphabet[25 - i % 26] + alphabet[25 - i % 26];
        } else if (i < 26 * 6) {
            return alphabet[25 - i % 26] + alphabet[25 - i % 26] + alphabet[25 - i % 26] + alphabet[25 - i % 26] + alphabet[25 - i % 26] + alphabet[25 - i % 26] + alphabet[25 - i % 26] ;
        } else if (i < 26 * 7) {
            return alphabet[25 - i % 26] + alphabet[25 - i % 26] + alphabet[25 - i % 26] + alphabet[25 - i % 26] + alphabet[25 - i % 26] + alphabet[25 - i % 26] + alphabet[25 - i % 26] + alphabet[25 - i % 26];
        } else {
            return alphabet[25 - i % 26] + alphabet[25 - i % 26] + alphabet[25 - i % 26] + alphabet[25 - i % 26] + alphabet[25 - i % 26] + alphabet[25 - i % 26] + alphabet[25 - i % 26] + alphabet[25 - i % 26] + alphabet[25 - i % 26];
        }

    }
}
