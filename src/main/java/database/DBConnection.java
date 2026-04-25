package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
        "jdbc:mysql://maglev.proxy.rlwy.net:22152/railway"
        + "?useSSL=false"
        + "&allowPublicKeyRetrieval=true"
        + "&serverTimezone=UTC"
        + "&connectTimeout=5000"
        + "&socketTimeout=5000";

    private static final String USERNAME = "root";
    private static final String PASSWORD = "xZCUnJHGNYYNIfeZmpzhIqOMzcsTuLmg";
    private static final boolean DEBUG = false;

    private DBConnection() {}

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);

            if (DEBUG) {
                System.out.println("[DBConnection] Connected to: " + conn.getCatalog());
            }
            return conn;

        } catch (ClassNotFoundException e) {
            throw new RuntimeException("[DBConnection] MySQL Driver not found", e);
        } catch (SQLException e) {
            throw new RuntimeException("[DBConnection] Connection failed: " + e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        System.out.println("Testing connection...");
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("SUCCESS — connected to: " + conn.getCatalog());
            }
        } catch (Exception e) {
            System.err.println("FAILED — " + e.getMessage());
        }
    }
}