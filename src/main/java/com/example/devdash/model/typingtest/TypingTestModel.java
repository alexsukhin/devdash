package com.example.devdash.model.typingtest;

import com.example.devdash.helper.data.SqliteConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Model sued for handling addition and removal of tasks.
 * Manages logic of Tasks within the database.
 *
 * Author: Alexander Sukhin
 * Version: 04/08/2025
 */
public class TypingTestModel {

    Connection connection;

    /**
     * Constructor to initialize the task model connection.
     */
    public TypingTestModel() {
        connection = SqliteConnection.Connector();
    }

    /**
     * Adds a new typing test session for the given user.
     */
    public boolean addSession(int userId, int testLength, boolean punctuation,
                              String startTime, String endTime,
                              double wpm, double accuracy) {
        String sql = "INSERT INTO TypingSession " +
                "(userId, testLength, punctuation, startTime, endTime, wpm, accuracy) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, testLength);
            stmt.setBoolean(3, punctuation);
            stmt.setString(4, startTime);
            stmt.setString(5, endTime);
            stmt.setDouble(6, wpm);
            stmt.setDouble(7, accuracy);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all typing sessions for a given user as a list of TypingSession objects.
     */
    public List<TypingSession> getSessions(int userId) {
        String sql = """
        SELECT ts.*, u.username
        FROM TypingSession ts
        INNER JOIN User u ON ts.userId = u.id
        WHERE ts.userId = ?
        ORDER BY ts.startTime DESC
        """;
        List<TypingSession> sessions = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                int testLength = rs.getInt("testLength");
                boolean punctuation = rs.getBoolean("punctuation");
                String startTime = rs.getString("startTime");
                String endTime = rs.getString("endTime");
                double wpm = rs.getDouble("wpm");
                double accuracy = rs.getDouble("accuracy");
                String username = rs.getString("username");

                sessions.add(new TypingSession(id, userId, testLength, punctuation, startTime, endTime, wpm, accuracy, username));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sessions;
    }

    public List<TypingSession> getQuickestSessions() {
        List<TypingSession> sessions = new ArrayList<>();
        String sql = """
        SELECT ts.*, u.username
        FROM TypingSession ts
        INNER JOIN (
            SELECT userId, MAX(wpm) AS max_wpm
            FROM TypingSession
            GROUP BY userId
        ) best
        ON ts.userId = best.userId AND ts.wpm = best.max_wpm
        INNER JOIN User u ON ts.userId = u.id
        ORDER BY ts.wpm DESC;

""";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                int userId = rs.getInt("userId");
                int testLength = rs.getInt("testLength");
                boolean punctuation = rs.getBoolean("punctuation");
                String startTime = rs.getString("startTime");
                String endTime = rs.getString("endTime");
                double wpm = rs.getDouble("wpm");
                double accuracy = rs.getDouble("accuracy");
                String username = rs.getString("username");

                sessions.add(new TypingSession(id, userId, testLength, punctuation, startTime, endTime, wpm, accuracy, username));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sessions;
    }


}
