package com.example.devdash.model.typingtest;

import java.time.LocalDateTime;

public class TypingSession {
    private int id;
    private int userId;
    private int testLength;
    private boolean punctuation;
    private String startTime;
    private String endTime;
    private double wpm;
    private double accuracy;
    private String username;

    // Constructor
    public TypingSession(int id, int userId, int testLength, boolean punctuation,
                         String startTime, String endTime, double wpm, double accuracy, String username) {
        this.id = id;
        this.userId = userId;
        this.testLength = testLength;
        this.punctuation = punctuation;
        this.startTime = startTime;
        this.endTime = endTime;
        this.wpm = wpm;
        this.accuracy = accuracy;
        this.username = username;
    }

    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getTestLength() { return testLength; }
    public boolean isPunctuation() { return punctuation; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public double getWpm() { return wpm; }
    public double getAccuracy() { return accuracy; }
    public String getUsername() { return username; }
}
