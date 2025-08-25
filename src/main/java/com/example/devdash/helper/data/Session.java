package com.example.devdash.helper.data;

import com.example.devdash.model.auth.User;

/**
 * Global session for storing the current logged-in user.
 *
 *  * Author: Alexander Sukhin
 *  * Version: 05/08/2025
 */
public class Session {
    private static Session instance;

    private User currentUser;

    private Session() {
    }

    /**
     * Returns the singleton instance of Session, creating it if needed.
     *
     * @return the global Session instance
     */
    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    /**
     * Sets the currently logged-in user for this session.
     *
     * @param user The User object to set as current user
     */
    public void setUser(User user) {
        this.currentUser = user;
    }

    /**
     * Returns the currently logged-in user, or null if no user is logged in.
     *
     * @return The current User or null
     */
    public User getUser() {
        return currentUser;
    }


    /**
     * Method to get the ID of the current user.
     * Returns -1 if no user is logged in.
     *
     * @return user ID or -1 if no user
     */
    public int getUserId() {
        return currentUser != null ? currentUser.getID() : -1;
    }

    /**
     * Clears the current session, removing any logged-in user.
     */
    public void clear() {
        currentUser = null;
    }

    /**
     * Returns true if a user is currently logged in, false otherwise.
     *
     * @return true if logged in, false if not
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
