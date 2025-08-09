package com.example.devdash.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Represents a GitHub commit with useful info and behavior.
 *
 * Author: Alexander Sukhin
 * Version: 05/08/2025
 */
public class Commit {

    private final String sha;
    private final String message;
    private final String url;
    private final LocalDateTime committedAt;

    public Commit(String sha, String message, String url) {
        this.sha = sha;
        this.message = message;
        this.url = url;
        this.committedAt = LocalDateTime.now();
    }

    public String getSha() {
        return sha;
    }

    public String getMessage() {
        return message;
    }

    public String getUrl() {
        return url;
    }

    public LocalDateTime getCommittedAt() {
        return committedAt;
    }

    /**
     * Returns a short version of the commit message (max 50 chars).
     */
    public String getShortMessage() {
        if (message.length() <= 50) {
            return message;
        }
        return message.substring(0, 47) + "...";
    }

    /**
     * Returns the committed date/time formatted nicely.
     */
    public String getFormattedCommittedAt() {
        return committedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    @Override
    public String toString() {
        return String.format("Commit %s: %s", sha.substring(0, 7), getShortMessage());
    }
}
