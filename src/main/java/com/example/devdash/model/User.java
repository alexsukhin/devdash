package com.example.devdash.model;

/**
 * Represents a user.
 *
 * Author: Alexander Sukhin
 * Version: 04/08/2025
 */
public class User {
    private String username;
    private String firstName;
    private String lastName;
    private String accessToken;
    private final int id;

    /**
     * Constructs a User object with the given attributes.
     *
     * @param username  The user's username
     * @param firstName The user's first name
     * @param lastName  The user's last name
     * @param accessToken The user's access token
     * @param id        The user's ID
     */
    public User(String username, String firstName, String lastName, String accessToken, int id) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.accessToken = accessToken;
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
     * @return The user's GitHub username
     */
    public String getAccessToken() { return accessToken; }

    /**
     * @return The user's unique ID
     */
    public int getID() { return id; }


    /**
     * Sets the user's GitHub username.
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}