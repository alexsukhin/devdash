package com.example.devdash.model;

public class User {
    private String username;
    private int id;

    public User(String username, int id) {
        this.username = username;
        this.id = id;
    }

    // Getters/setters
    public String getUsername() { return username; }
    public int getId() { return id; }
}