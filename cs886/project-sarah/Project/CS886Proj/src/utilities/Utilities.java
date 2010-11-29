package utilities;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author snadi
 */
public abstract class Utilities {

    private static String months[] = {"Jan", "Feb", "March", "Apr", "May", "Jun", "July", "Aug", "Sep", "Oct", "Nov", "Dec"};

    public static Connection getLocalConnection() {
        try {
            String driver = "com.mysql.jdbc.Driver";
            String url = "jdbc:mysql://localhost:3306/ca_data";
            String username = "root";
            String password = "caworld";
            Class.forName(driver).newInstance();
            return DriverManager.getConnection(url, username, password);
        } catch (Exception ex) {
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    public static String getDisplayDate(java.util.Date date) {
        SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        return sd.format(date);
    }

    public static String getMySQLDate(java.util.Date date) {
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
        return sd.format(date);
    }

    public static String formatTime(int timeInSec) {
        int days, hours, minutes;
        days = timeInSec / (3600 * 24);
        timeInSec = timeInSec - (days * 3600 * 24);
        hours = timeInSec / 3600;

        timeInSec = timeInSec - hours * 3600;
        minutes = timeInSec / 60;
        timeInSec = timeInSec - minutes * 60;

        return days + "d " + hours + "h " + minutes + "m " + timeInSec + "s";
    }

    public static java.util.Date convertToDate(int intDate) {

        return new java.util.Date(((long) intDate) * 1000);
    }

    public static String getDisplayDate(int intDate) {
        return convertToDate(intDate).toString();
    }


    public static String getMonth(int i) {
        int index = i % 12;

        return months[index];

    }

    public static ResultSet executeQuery(Connection conn, String query) throws SQLException {
        return conn.createStatement().executeQuery(query);
    }

    public static String getString(Vector stringVector) {
        String names = "";
        for (int i = 0; i < stringVector.size(); i++) {
            names += stringVector.get(i) + "; ";
        }
        return names;
    }

    public static String getSeperateLines(Vector stringVector) {
        String names = "";
        for (int i = 0; i < stringVector.size(); i++) {
            names += stringVector.get(i) + "\n";
        }
        return names;
    }

    public static void printToFile(String input, String fileName) {
        PrintWriter filePrinter = null;
        try {
            File resourceFile = new File(fileName);
            filePrinter = new PrintWriter(resourceFile);
            filePrinter.println(input);
            filePrinter.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            filePrinter.close();
        }
    }

    /**
     * Reallocates an array with a new size, and copies the contents
     * of the old array to the new array.
     * @param oldArray  the old array, to be reallocated.
     * @param newSize   the new array size.
     * @return          A new array with the same contents.
     */
    public static Object resizeArray(Object oldArray, int newSize) {
        int oldSize = java.lang.reflect.Array.getLength(oldArray);
        Class elementType = oldArray.getClass().getComponentType();
        Object newArray = java.lang.reflect.Array.newInstance(
                elementType, newSize);
        int preserveLength = Math.min(oldSize, newSize);
        if (preserveLength > 0) {
            System.arraycopy(oldArray, 0, newArray, 0, preserveLength);
        }
        return newArray;
    }
}
