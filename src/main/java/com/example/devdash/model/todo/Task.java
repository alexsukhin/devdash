package com.example.devdash.model.todo;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single to-do task.
 *
 * Author: Alexander Sukhin
 * Version: 31/08/2025
 */
public class Task {


    private int id;
    private String description;
    private String status;
    private int priority; // 0 = low, 1 = medium, 2 = high
    private String dueDate;
    private LocalDateTime createdAt;
    private String updatedAt;


    /**
     * Constructs a Task with id, description, and completion status.
     * Creation time is set to current date/time.
     *
     * @param id Task's ID
     * @param description Task's description
     */
    public Task(int id, String description, String status, int priority, String dueDate, String updatedAt) {
        this.id = id;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.dueDate = dueDate;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = updatedAt;
    }

    /**
     * @return The task's id
     */
    public int getId() {
        return id;
    }

    /**
     * @return The task's description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return The task status
     */
    public String getStatus() { return status; }

    /**
     * @return The task priority (0 = low, 1 = medium, 2 = high).
     */
    public int getPriority() { return priority; }

    /**
     * Returns formatted due date or empty string if null.
     */
    public String getDueDate() {
        return dueDate != null ? dueDate : "";
    }

    /**
     * Returns a human-readable string representing how long ago the task was last updated.
     *
     * @return A formatted string describing the relative update time
     */
    public String getFormattedUpdatedAt() {
        if (updatedAt == null || updatedAt.isBlank()) return "";


        // parse the updatedAt string into LocalDateTime
        LocalDateTime updatedTime = LocalDateTime.parse(updatedAt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime now = LocalDateTime.now();

        Duration duration = Duration.between(updatedTime, now);

        long seconds = duration.getSeconds();
        if (seconds < 60) {
            return "Updated just now";
        } else if (seconds < 3600) { // less than 1 hour
            long minutes = seconds / 60;
            return "Updated " + minutes + " minute" + (minutes > 1 ? "s" : "") + " ago";
        } else if (seconds < 86400) { // less than 1 day
            long hours = seconds / 3600;
            return "Updated " + hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        } else {
            long days = seconds / 86400;
            return "Updated " + days + " day" + (days > 1 ? "s" : "") + " ago";
        }
    }
}
