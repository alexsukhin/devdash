package com.example.devdash.model.todo;

import java.time.LocalDate;
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
    private String status;
    private int priority; // 0 = low, 1 = medium, 2 = high
    private String dueDate;
    private int position;
    private LocalDateTime createdAt;


    /**
     * Constructs a Task with id, description, and completion status.
     * Creation time is set to current date/time.
     *
     * @param id Task's ID
     * @param description Task's description
     */
    public Task(int id, String description, String status, int priority, String dueDate, int position) {
        this.id = id;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.dueDate = dueDate;
        this.position = position;
        this.createdAt = LocalDateTime.now();
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
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    /**
     * @return The task's creation date and time as a LocalDateTime
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    /**
     * Returns the creation timestamp formatted as a string.
     * Format: "yyyy-MM-dd HH:mm"
     *
     * @return Formatted creation date/time string
     */
    public String getFormattedCreatedAt() {
        return createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    /**
     * Returns formatted due date or empty string if null.
     */
    public String getDueDate() {
        return dueDate != null ? dueDate : "";
    }

    //dueDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
}
