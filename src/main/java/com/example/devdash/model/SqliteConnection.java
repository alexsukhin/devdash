package com.example.devdash.model;
import java.sql.*;

public class SqliteConnection {

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
