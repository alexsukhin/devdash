package com.example.devdash.helper;

import com.example.devdash.model.User;

/**
 * Global session for storing the current logged-in user.
 */
public class Session {
    private static Session instance;

    private User currentUser;

    private Session() {}

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public void setUser(User user) {
        this.currentUser = user;
    }

    public User getUser() {
        return currentUser;
    }

    public int getUserId() {
        return currentUser != null ? currentUser.getId() : -1;
    }

    public void clear() {
        currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
