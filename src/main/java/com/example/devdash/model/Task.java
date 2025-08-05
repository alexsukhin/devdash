package com.example.devdash.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single to-do task.
 *
 * Author: Alexander Sukhin
 * Version: 05/08/2025
 */
public class Task {

    private int id;
    private String description;
    private LocalDateTime createdAt;
    private boolean completed;

    public Task(int id, String description) {
        this.id = id;
        this.description = description;
        this.createdAt = LocalDateTime.now();
        this.completed = false;
    }


    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getFormattedCreatedAt() {
        return createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
