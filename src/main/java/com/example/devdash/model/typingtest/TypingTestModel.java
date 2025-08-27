package com.example.devdash.model.typingtest;

import com.example.devdash.helper.data.SqliteConnection;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class for managing typing test sessions within the database.
 * Handles adding new sessions and retrieving session data for users.
 *
 * @author Alexander Sukhin
 * @version 28/08/2025
 */
public class TypingTestModel {

    Connection connection;

    /**
     * Constructor to initialize the TypingTestModel with a database connection.
     */
    public TypingTestModel() {
        connection = SqliteConnection.Connector();
    }

    /**
     * Adds a new typing test session record for a specific user.
     *
     * @param userId      The user's id
     * @param testLength  The typing test length
     * @param punctuation Whether punctuation was used
     * @param startTime   Start time of the test
     * @param endTime     End time of the test
     * @param wpm         Words per minute
     * @param accuracy    Accuracy percentage
     */
    public void addSession(int userId, int testLength, boolean punctuation,
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Retrieves all typing test sessions for a given user.
     *
     * @param userId The user's id
     * @return a list of TypingSession objects representing the user's typing sessions
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

    /**
     * Retrieves the highest WPM sessions for each user filtered by test length and punctuation.
     *
     * @param testLength The length of the test to filter by
     * @param punctuation Whether punctuation was included to filter by
     * @return a list of TypingSession objects representing the quickest sessions per user
     */
    public List<TypingSession> getQuickestSessions(int testLength, boolean punctuation) {
        List<TypingSession> sessions = new ArrayList<>();
        String sql = """
        SELECT ts.*, u.username
        FROM TypingSession ts
        INNER JOIN (
            SELECT userId, MAX(wpm) AS max_wpm
            FROM TypingSession
            WHERE testLength = ? AND punctuation = ?
            GROUP BY userId
        ) best
        ON ts.userId = best.userId AND ts.wpm = best.max_wpm
        INNER JOIN User u ON ts.userId = u.id
        WHERE ts.testLength = ? AND ts.punctuation = ?
        ORDER BY ts.wpm DESC;

""";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, testLength);
            stmt.setBoolean(2, punctuation);
            stmt.setInt(3, testLength);
            stmt.setBoolean(4, punctuation);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                int userId = rs.getInt("userId");
                int length = rs.getInt("testLength");
                boolean hasPunctuation = rs.getBoolean("punctuation");
                String startTime = rs.getString("startTime");
                String endTime = rs.getString("endTime");
                double wpm = rs.getDouble("wpm");
                double accuracy = rs.getDouble("accuracy");
                String username = rs.getString("username");

                sessions.add(new TypingSession(id, userId, length, hasPunctuation, startTime, endTime, wpm, accuracy, username));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sessions;
    }


}
