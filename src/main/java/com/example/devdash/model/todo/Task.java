package com.example.devdash.model.todo;

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


    /**
     * Constructs a Task with id, description, and completion status.
     * Creation time is set to current date/time.
     *
     * @param id Task's ID
     * @param description Task's description
     * @param completed Current status of task
     */
    public Task(int id, String description, boolean completed) {
        this.id = id;
        this.description = description;
        this.createdAt = LocalDateTime.now();
        this.completed = completed;
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
     * @return The task's creation date and time as a LocalDateTime
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * @return True if the task is completed, false otherwise
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Sets the completion status of the task.
     *
     * @param completed The new completion status
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    /**
     * Returns the creation timestamp formatted as a string.
     * Format: "yyyy-MM-dd HH:mm"
     *
     * @return Formatted creation date/time string
     */
    public String getFormattedCreatedAt() {
        return createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
