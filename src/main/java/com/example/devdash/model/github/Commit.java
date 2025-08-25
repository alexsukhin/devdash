package com.example.devdash.model.github;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a GitHub commit. Holds commit SHA,
 * message, URL, and timestamp
 *
 * Author: Alexander Sukhin
 * Version: 05/08/2025
 */
public class Commit {

    private final String sha;
    private final String message;
    private final String url;
    private final LocalDateTime committedAt;


    /**
     * Constructor for the commit.
     *
     * @param sha        The full commit SHA hash string
     * @param message    The commit message text
     * @param url        URL to the commit on GitHub
     * @param commitedAt The time the commit was pushed
     */
    public Commit(String sha, String message, String url, LocalDateTime commitedAt) {
        this.sha = sha;
        this.message = message;
        this.url = url;
        this.committedAt = commitedAt;
    }

    /**
     * @return Commit SHA string
     */
    public String getSha() {
        return sha;
    }

    /**
     * @return Commit message string
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return Commit URL string
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return Commit timestamp as LocalDateTime
     */
    public LocalDateTime getCommittedAt() {
        return committedAt;
    }

    /**
     * Returns a short version of the commit message (max 50 chars).
     *
     * @return Shortened commit message string
     */
    public String getShortMessage() {
        if (message.length() <= 50) {
            return message;
        }
        return message.substring(0, 47) + "...";
    }

    /**
     * Returns the committed date/time formatted nicely.
     *
     * @return Formatted commit date-time string
     */
    public String getFormattedCommittedAt() {
        return committedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    /**
     * Returns a human-readable string representation of the commit.
     *
     * @return String representation of the commit
     */
    @Override
    public String toString() {
        return String.format("Commit %s: %s", sha.substring(0, 7), getShortMessage());
    }
}
