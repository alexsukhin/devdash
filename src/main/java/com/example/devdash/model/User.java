package com.example.devdash.model;

public class User {
    private String username;
    private String firstName;
    private String lastName;
    private int id;

    public User(String username, String firstName, String lastName, int id) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = id;
    }

    public String getUsername() { return username; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public int getId() { return id; }
}