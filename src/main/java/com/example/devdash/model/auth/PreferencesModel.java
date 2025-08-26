package com.example.devdash.model.auth;

import com.example.devdash.helper.data.SqliteConnection;
import java.sql.*;

/**
 * Model class for handling user preferences.
 *
 * Author: Alexander Sukhin
 * Version: 20/08/2025
 */
public class PreferencesModel {
    private static PreferencesModel instance;
    private Connection connection;

    private PreferencesModel() {
        connection = SqliteConnection.Connector();
    }

    /**
     * Singleton accessor
     */
    public static PreferencesModel getInstance() {
        if (instance == null) {
            instance = new PreferencesModel();
        }
        return instance;
    }

    /**
     * Checks if DB connection is valid
     */
    public boolean isDbConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            System.err.println("Error checking DB connection: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get dark mode preference
     */
    public boolean getDarkMode(int userId) {
        try {
            String sql = "SELECT darkMode FROM UserPreferences WHERE userId = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getBoolean("darkMode");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // default
    }

    /**
     * Update dark mode preference
     */
    public void updateDarkMode(int userId, boolean darkMode) {
        try {
            String sql = "UPDATE UserPreferences SET darkMode = ? WHERE userId = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setBoolean(1, darkMode);
                stmt.setInt(2, userId);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get focus time
     */
    public int getFocusTime(int userId) {
        try {
            String sql = "SELECT focusTime FROM UserPreferences WHERE userId = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("focusTime");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 25; // default
    }

    /**
     * Update focus time
     */
    public void updateFocusTime(int userId, int focusTime) {
        try {
            String sql = "UPDATE UserPreferences SET focusTime = ? WHERE userId = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, focusTime);
                stmt.setInt(2, userId);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get break time
     */
    public int getBreakTime(int userId) {
        try {
            String sql = "SELECT breakTime FROM UserPreferences WHERE userId = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("breakTime");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 5; // default
    }

    /**
     * Update break time
     */
    public void updateBreakTime(int userId, int breakTime) {
        try {
            String sql = "UPDATE UserPreferences SET breakTime = ? WHERE userId = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, breakTime);
                stmt.setInt(2, userId);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get typing test length
     */
    public int getTestLength(int userId) {
        try {
            String sql = "SELECT testLength FROM UserPreferences WHERE userId = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("testLength");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 10; // default
    }

    /**
     * Update typing test length
     */
    public void updateTestLength(int userId, int testLength) {
        try {
            String sql = "UPDATE UserPreferences SET testLength = ? WHERE userId = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, testLength);
                stmt.setInt(2, userId);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get typing test length
     */
    public boolean getPunctuationBool(int userId) {
        try {
            String sql = "SELECT punctuation FROM UserPreferences WHERE userId = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getBoolean("punctuation");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // default
    }

    /**
     * Update typing test length
     */
    public void updatePunctuationBool(int userId, int punctuation) {
        try {
            String sql = "UPDATE UserPreferences SET punctuation = ? WHERE userId = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, punctuation);
                stmt.setInt(2, userId);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
