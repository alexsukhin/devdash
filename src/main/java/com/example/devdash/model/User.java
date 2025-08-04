package com.example.devdash.model;

/**
 * Represents a user.
 *
 * Author: Alexander Sukhin
 * Version: 04/08/2025
 */
public class User {
    private final String username;
    private final String firstName;
    private final String lastName;
    private final int id;

    /**
     * Constructs a User object with the given attributes.
     *
     * @param username  The user's username
     * @param firstName The user's first name
     * @param lastName  The user's last name
     * @param id        The user's ID
     */
    public User(String username, String firstName, String lastName, int id) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = id;
    }

    /**
     * @return The user's username
     */
    public String getUsername() { return username; }

    /**
     * @return The user's first name
     */
    public String getFirstName() { return firstName; }


    /**
     * @return The user's last name
     */
    public String getLastName() { return lastName; }

    /**
     * @return The user's unique ID
     */
    public int getId() { return id; }
}