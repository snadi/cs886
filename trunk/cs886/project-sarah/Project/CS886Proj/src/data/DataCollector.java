/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.TreeBidiMap;
import utilities.Utilities;

/**
 *
 * @author nadsa02
 */
public class DataCollector {

    private int fromDate;
    private int toDate;
    private BidiMap idToIndexMap;
    private BidiMap idToNameMap;

    public DataCollector(int fromDate, int toDate) {
        System.out.println("Data Collector constructor");
        idToIndexMap = new TreeBidiMap();

        idToNameMap = new TreeBidiMap();
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public Hashtable<String, Vector<ChangedCI>> collectData(boolean removeUnary) {
        try {

            ResultSet rs = null;
            Hashtable<String, Vector<ChangedCI>> reportSet = new Hashtable<String, Vector<ChangedCI>>();

            String query = "Select open_date, chg.chg_ref_num, own_resource_uuid " +
                    "from lrel join chg on " +
                    "lrel.l_persid = chg.persid " +
                    "join ca_owned_resource on " +
                    "lrel.r_persid = concat('nr:',own_resource_uuid) " +
                    "where open_date >= " + fromDate + " and open_date <= " + toDate + " " +                    
                    "group by chg_ref_num, own_resource_uuid, open_Date  " +
                    "order by chg.chg_ref_num, open_date, own_Resource_uuid";
            //return order as ref, open date, uuid


            rs = Utilities.executeQuery(Utilities.getLocalConnection(), query);

            while (rs.next()) {                
                ChangedCI changeOrder = new ChangedCI("X" + rs.getString("own_resource_uuid"));

                Vector<ChangedCI> vector = reportSet.get(rs.getString("chg_ref_num"));

                if (vector == null) {
                    vector = new Vector<ChangedCI>();
                }

                if (!vector.contains(changeOrder)) {
                    vector.add(changeOrder);
                }

                reportSet.put(rs.getString("chg_ref_num"), vector);
            }

            if (removeUnary) {
                removeUnarySets(reportSet);
            }


            return reportSet;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Hashtable<String, Vector<ChangedCI>> collectData(Connection conn, boolean removeUnary) {
        try {

            ResultSet rs = null;
            Hashtable<String, Vector<ChangedCI>> reportSet = new Hashtable<String, Vector<ChangedCI>>();

            String query = "Select open_date, chg.chg_ref_num, own_resource_uuid " +
                    "from lrel join chg on " +
                    "lrel.l_persid = chg.persid " +
                    "join ca_owned_resource on " +
                    "lrel.r_persid = 'nr:' + UPPER(SUBSTRING(master.dbo.fn_varbintohexstr(own_resource_uuid), 3, 33)) " +
                    "where open_date >= " + fromDate + " and open_date <= " + toDate + " " +
                    // "and lrel.l_persid in (select distinct(lrel.l_persid) from lrel group by l_persid having count(r_persid) >1 ) " +
                    "group by chg_ref_num, own_resource_uuid, open_Date " +
                    "order by chg.chg_ref_num, open_date, own_resource_uuid";


            rs = Utilities.executeQuery(conn, query);

            while (rs.next()) {
                // System.out.println("start date: " + rs.getInt("open_date"));
                ChangedCI changeOrder = new ChangedCI( rs.getString("own_resource_uuid"));

                Vector<ChangedCI> vector = reportSet.get(rs.getString("chg_ref_num"));

                if (vector == null) {
                    vector = new Vector<ChangedCI>();
                }

                if (!vector.contains(changeOrder)) {
                    vector.add(changeOrder);
                } 

                reportSet.put(rs.getString("chg_ref_num"), vector);
            }

            if (removeUnary) {
                removeUnarySets(reportSet);
            }


            return reportSet;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Hashtable<String, Vector<ChangedCI>> collectDataFake(String fileName) {
        Hashtable<String, Vector<ChangedCI>> reportSet = new Hashtable<String, Vector<ChangedCI>>();
        try {

            FileInputStream fileInputStream = null;
            File file = new File(fileName);
            fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);
            String line = dataInputStream.readLine();
            int count = 1;

            while (line != null) {
                // System.out.println("line: " + line);
                String[] parts = line.split(" ");
                Vector<ChangedCI> vector = new Vector<ChangedCI>();

                for (int i = 0; i < parts.length; i++) {
                    vector.add(new ChangedCI(parts[i]));
                }

                reportSet.put(String.valueOf(count), vector);
                count++;
                line = dataInputStream.readLine();
            }

            return reportSet;
        } catch (IOException ex) {
            ex.printStackTrace();
            return reportSet;
        }
    }

    public void createGraphNodes() {
        try {
            System.out.println("createGraphNodes() started");
            String query = "Select own_resource_uuid, resource_name, count(chg_ref_num) as cnt  " +
                    "from mdb.dbo.lrel join mdb.dbo.chg on " +
                    "mdb.dbo.lrel.l_persid = mdb.dbo.chg.persid " +
                    "join mdb.dbo.ca_owned_resource on " +
                    "mdb.dbo.lrel.r_persid = 'nr:' + UPPER(SUBSTRING(master.dbo.fn_varbintohexstr(own_resource_uuid), 3, 33)) " +
                    "where open_date >=  " + fromDate + " and open_date <= " + toDate + " " +
                    //"and lrel.l_persid in (select distinct(lrel.l_persid) from lrel group by l_persid having count(r_persid) >1 ) " +
                    "group by own_resource_uuid, resource_name " +
                    "order by own_resource_uuid";

            Connection conn = Utilities.getLocalConnection();
            ResultSet rs = Utilities.executeQuery(conn, query);

            int index = 0;

            while (rs.next()) {
                String uuid = rs.getString("own_resource_uuid");
                int count = rs.getInt("cnt");
                idToIndexMap.put(uuid, index);
                idToNameMap.put(uuid, rs.getString("resource_name"));
                index++;
            }

            readExampleSet(index, idToIndexMap, idToNameMap);

            System.out.println("finished with index: " + index);

            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void readExampleSet(int index, BidiMap idToIndexMap, BidiMap idToNameMap) {
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader("exampleIds.txt"));

            String entry = fileReader.readLine();
            while (entry != null) {
                //    System.out.println("Looping nodes");
                String[] parts = entry.split(";");
                if (parts.length == 2) {
                    //     System.out.println("found 3 parts");
                    String id = parts[0];
                    String name = parts[1];
                    idToIndexMap.put(id, index++);
                    idToNameMap.put(id, name);
                }

                entry = fileReader.readLine();
            }
        } catch (IOException ex) {
            Logger.getLogger(DataCollector.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    public void createGraphNodes(Connection conn) {
        try {
            System.out.println("createGraphNodes() started");
            String query = "Select own_resource_uuid, resource_name, count(chg_ref_num) as cnt  " +
                    "from mdb.dbo.lrel join mdb.dbo.chg on " +
                    "mdb.dbo.lrel.l_persid = mdb.dbo.chg.persid " +
                    "join mdb.dbo.ca_owned_resource on " +
                    "mdb.dbo.lrel.r_persid = 'nr:' + UPPER(SUBSTRING(master.dbo.fn_varbintohexstr(own_resource_uuid), 3, 33)) " +
                    "where open_date >=  " + fromDate + " and open_date <= " + toDate + " " +
                    //"and lrel.l_persid in (select distinct(lrel.l_persid) from lrel group by l_persid having count(r_persid) >1 ) " +
                    "group by own_resource_uuid, resource_name " +
                    "order by own_resource_uuid";

            ResultSet rs = Utilities.executeQuery(conn, query);

            int index = 0;

            while (rs.next()) {
                String uuid = rs.getString("own_resource_uuid");
                int count = rs.getInt("cnt");
                idToIndexMap.put(uuid, index);
                idToNameMap.put(uuid, rs.getString("resource_name"));
                index++;
            }

            System.out.println("finished with index: " + index);

            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void createGraphNodesFake(String fileName) {

        try {
            FileInputStream fileInputStream = null;
            File file = new File(fileName);
            fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);
            String line = dataInputStream.readLine();
            Hashtable<String, Integer> countMap = new Hashtable<String, Integer>();

            while (line != null) {
                String[] parts = line.split(" ");

                for (int i = 0; i < parts.length; i++) {
                    Integer idCount = countMap.get(parts[i]);

                    if (idCount == null) {
                        countMap.put(parts[i], 1);
                    } else {
                        countMap.put(parts[i], idCount + 1);
                    }
                }
                line = dataInputStream.readLine();
            }

            int index = 0;

            for (Enumeration e = countMap.keys(); e.hasMoreElements();) {
                String key = (String) e.nextElement();
                Integer count = countMap.get(key);
                idToIndexMap.put(key, index);
                idToNameMap.put(key, key);
                index++;
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public BidiMap getIdToIndexMap() {
        return idToIndexMap;
    }

    public void setIdToIndexMap(BidiMap idToIndexMap) {
        this.idToIndexMap = idToIndexMap;
    }

    public int getFromDate() {
        return fromDate;
    }

    public void setFromDate(int fromDate) {
        this.fromDate = fromDate;
    }

    public int getToDate() {
        return toDate;
    }

    public void setToDate(int toDate) {
        this.toDate = toDate;
    }

    public BidiMap getIdToNameMap() {
        return idToNameMap;
    }

    public void setIdToNameMap(BidiMap idToNameMap) {
        this.idToNameMap = idToNameMap;
    }

    private void removeUnarySets(Hashtable<String, Vector<ChangedCI>> reportSet) {
        for (String key : reportSet.keySet()) {
            if (reportSet.get(key).size() < 2) {
                reportSet.remove(key);
            }
        }
    }
}
