package com.example.devdash.model.typingtest;

/**
 * Represents a typing test session record for a user.
 * Stores metadata like test length, punctuation settings, WPM, accuracy, and timestamps.
 *
 * Author: Alexander Sukhin
 * Version: 28/08/2025
 */
public class TypingSession {

    private final int id;
    private final int userId;
    private final int testLength;
    private final boolean punctuation;
    private final String startTime;
    private final String endTime;
    private final double wpm;
    private final double accuracy;

    /**
     * Constructs a TypingSession object with all details.
     *
     * @param id          Unique session ID
     * @param userId      The user's id
     * @param testLength  The typing test length
     * @param punctuation Whether punctuation was used
     * @param startTime   Start time of the test
     * @param endTime     End time of the test
     * @param wpm         Words per minute
     * @param accuracy    Accuracy percentage
     */
    public TypingSession(int id, int userId, int testLength, boolean punctuation,
                         String startTime, String endTime, double wpm, double accuracy) {
        this.id = id;
        this.userId = userId;
        this.testLength = testLength;
        this.punctuation = punctuation;
        this.startTime = startTime;
        this.endTime = endTime;
        this.wpm = wpm;
        this.accuracy = accuracy;
    }

    /** @return Session ID */
    public int getId() {
        return id;
    }

    /** @return User ID */
    public int getUserId() {
        return userId;
    }

    /** @return Number of words in the test */
    public int getTestLength() {
        return testLength;
    }

    /** @return Whether punctuation was enabled */
    public boolean hasPunctuation() {
        return punctuation;
    }

    /** @return Start time of the test (formatted as string) */
    public String getStartTime() {
        return startTime;
    }

    /** @return End time of the test (formatted as string) */
    public String getEndTime() {
        return endTime;
    }

    /** @return Words per minute (WPM) */
    public double getWpm() {
        return wpm;
    }

    /** @return Accuracy percentage */
    public double getAccuracy() {
        return accuracy;
    }
}
