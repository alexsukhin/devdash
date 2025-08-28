package com.example.devdash.model.typingtest;

import com.example.devdash.helper.data.SqliteConnection;

import java.sql.*;
import java.time.LocalDate;
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

                sessions.add(new TypingSession(id, userId, testLength, punctuation, startTime, endTime, wpm, accuracy));
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
        SELECT ts.*
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

                sessions.add(new TypingSession(id, userId, length, hasPunctuation, startTime, endTime, wpm, accuracy));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sessions;
    }

    /**
     * Updates the typing streak for a given user.
     * Logic:
     * - If user typed today and also yesterday → increment streak.
     * - If user typed today but not yesterday → streak = 1.
     * - If last session was yesterday (but none today) → keep current streak.
     * - If last session was earlier → streak = 0.
     *
     * @param userId The user's id
     */
    public void updateStreak(int userId) {
        String lastSessionSql = "SELECT MAX(startTime) AS lastDate FROM TypingSession WHERE userId = ?";
        String streakSql = "UPDATE User SET typingStreak = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(lastSessionSql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            LocalDate today = LocalDate.now();
            LocalDate yesterday = today.minusDays(1);

            int newStreak = 0;

            if (rs.next()) {
                String lastDateStr = rs.getString("lastDate");
                if (lastDateStr != null) {
                    LocalDate lastDate = LocalDate.parse(lastDateStr.substring(0, 10)); // "yyyy-MM-dd"
                    int currentStreak = getUserStreak(userId);

                    if (lastDate.equals(today)) {
                        if (hadSessionOn(userId, yesterday)) {
                            newStreak = currentStreak + 1;
                        } else {
                            newStreak = 1;
                        }
                    } else if (lastDate.equals(yesterday)) {
                        newStreak = currentStreak;
                    } else {
                        newStreak = 0;
                    }
                }
            }

            try (PreparedStatement updateStmt = connection.prepareStatement(streakSql)) {
                updateStmt.setInt(1, newStreak);
                updateStmt.setInt(2, userId);
                updateStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks whether a user had at least one typing session on a specific date.
     *
     * @param userId The user's id
     * @param date   The date to check
     * @return true if a session exists on that date, false otherwise
     */
    private boolean hadSessionOn(int userId, LocalDate date) {
        String sql = "SELECT 1 FROM TypingSession WHERE userId = ? AND DATE(startTime) = ? LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, date.toString()); // "yyyy-MM-dd"
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves the current streak value for a given user.
     *
     * @param userId The user's id
     * @return the current typing streak (0 if none found)
     */
    public int getUserStreak(int userId) {
        String sql = "SELECT typingStreak FROM User WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("typingStreak");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


}
