package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String HOST     = "maglev.proxy.rlwy.net";
    private static final String PORT     = "22152";
    private static final String DATABASE = "railway";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "xZCUnJHGNYYNIfeZmpzhIqOMzcsTuLmg";

    private static final String URL =
            "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
            + "?useSSL=false"
            + "&allowPublicKeyRetrieval=true"
            + "&serverTimezone=Asia/Kolkata"
            + "&autoReconnect=true"
            + "&connectTimeout=10000";

    private static Connection connection = null;

    private DBConnection() {}

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("[DBConnection] Connected successfully.");
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("[DBConnection] Driver not found. Add mysql-connector-j to pom.xml", e);
        } catch (SQLException e) {
            throw new RuntimeException("[DBConnection] Connection failed: " + e.getMessage(), e);
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("[DBConnection] Connection closed.");
            } catch (SQLException e) {
                System.err.println("[DBConnection] Error closing: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Testing connection...");
        try {
            Connection conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("SUCCESS — connected to: " + conn.getCatalog());
            }
        } catch (Exception e) {
            System.err.println("FAILED — " + e.getMessage());
        } finally {
            closeConnection();
        }
    }
}