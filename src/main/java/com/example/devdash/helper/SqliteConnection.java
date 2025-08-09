package com.example.devdash.helper;
import java.sql.*;

/**
 * Establishes a connection to the SQLite database.
 *
 * Author: Alexander Sukhin
 * Version: 04/08/2025
 */
public class SqliteConnection {

    private static Connection connection;

    /**
     * Connects to the SQLite database stored at the specified file path.
     *
     * @return Connection if successful, otherwise null.
     */
    public static Connection Connector() {
        if (connection == null) {
            try {
                String url = "jdbc:sqlite:C:/Users/alex/AppData/Roaming/DevDash/database.db";
                connection = DriverManager.getConnection(url);
                System.out.println("Connected to SQLite database");
            } catch (SQLException e) {
                e.printStackTrace();
                connection = null;
            }
        }
        return connection;
    }

}