package com.example.devdash.model;
import java.sql.*;

/**
 * Establishes a connection to the SQLite database.
 *
 * Author: Alexander Sukhin
 * Version: 04/08/2025
 */
public class SqliteConnection {

    /**
     * Connects to the SQLite database stored at the specified file path.
     *
     * @return Connection if successful, otherwise null.
     */
    public static Connection Connector() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:C:/Users/alex/AppData/Roaming/DevDash/database.db");
            return conn;

        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

}
