package com.example.devdash.model.todo;

import java.time.LocalDate;

public class Sprint {
    private int id;
    private int userId;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;

    public Sprint(int id, String name) {
        this.id = id;
        this.userId = 0;
        this.name = name;
        this.startDate = null;
        this.endDate = null;
    }

    public Sprint(int id, int userId, String name, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
