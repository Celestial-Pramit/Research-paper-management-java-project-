package com.researchpapers.utill;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionSingleton {

    private static final String URL = "jdbc:mysql://localhost:3306/research_papers";
    private static final String USER = "root";
    private static final String PASSWORD = "12345!!";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    private static Connection connection;

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC driver not found: " + e.getMessage(), e);
        }
    }

    private ConnectionSingleton() {}

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database connection failed: " + e.getMessage(), e);
        }
        return connection;
    }

    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed() && conn.isValid(3);
        } catch (Exception e) {
            System.err.println("Database connectivity test failed: " + e.getMessage());
            return false;
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                System.err.println("Failed to close connection: " + e.getMessage());
            }
        }
    }
}
