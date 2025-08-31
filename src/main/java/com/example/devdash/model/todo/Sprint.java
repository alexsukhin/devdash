package com.example.devdash.model.todo;

import java.time.LocalDate;

/**
 * Represents a Sprint in the to-do system.
 *
 * Author: Alexander Sukhin
 * Version: 31/08/2025
 */
public class Sprint {
    private int id;
    private int userId;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;

    /**
     * Constructs a sprint with only an ID and name.
     * Useful for No Sprint options
     *
     * @param id   The sprint's ID
     * @param name the name of the sprint
     */
    public Sprint(int id, String name) {
        this.id = id;
        this.userId = 0;
        this.name = name;
        this.startDate = null;
        this.endDate = null;
    }

    /**
     * Constructs a sprint with full details.
     *
     * @param id        The sprint's ID
     * @param userId    The user's id
     * @param name      the name of the sprint
     * @param startDate the sprint start date
     * @param endDate   the sprint end date
     */
    public Sprint(int id, int userId, String name, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * @return The sprint's ID
     */
    public int getId() {
        return id;
    }

    /**
     * @return The sprint's name
     */
    public String getName() {
        return name;
    }
}
