package com.example.devdash.model;

import com.example.devdash.helper.SqliteConnection;

import java.sql.*;

/**
 * Model for managing Pomodoro session data.
 * Handles insertion, updating, and querying of Pomodoro sessions.
 *
 * Author: Alexander Sukhin
 * Version: 20/08/2025
 */
public class PomodoroModel {

    private final Connection connection;

    public PomodoroModel() {
        connection = SqliteConnection.Connector();
    }

    /**
     * Adds a new Pomodoro session.
     */
    public void addSession(int userId, String startTime, String endTime, int duration, boolean completed) {
        String sql = "INSERT INTO PomodoroSession (userId, startTime, endTime, duration, completed) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, startTime);
            pstmt.setString(3, endTime);
            pstmt.setInt(4, duration);
            pstmt.setBoolean(5, completed);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding Pomodoro session", e);
        }
    }

    /**
     * Updates an existing Pomodoro session when it ends.
     */
    public void endSession(int sessionId, String endTime, int duration, boolean completed) {
        String sql = "UPDATE PomodoroSession SET endTime = ?, duration = ?, completed = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, endTime);
            pstmt.setInt(2, duration);
            pstmt.setBoolean(3, completed);
            pstmt.setInt(4, sessionId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error ending Pomodoro session", e);
        }
    }

    /**
     * Returns the last inserted session ID.
     */
    public int getLastInsertedId() {
        String sql = "SELECT last_insert_rowid() as lastId";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("lastId");
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching last inserted ID", e);
        }
        return -1;
    }

    /**
     * Gets total duration of sessions today for a given user.
     * Uses localtime to ensure correct date across midnight.
     */
    public int getTodayTotalDuration(int userId) {
        String sql = "SELECT SUM(duration) AS total_seconds " +
                "FROM PomodoroSession " +
                "WHERE userId = ? AND DATE(startTime, 'localtime') = DATE('now', 'localtime')";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt("total_seconds");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching today's total duration", e);
        }
        return 0;
    }
}
