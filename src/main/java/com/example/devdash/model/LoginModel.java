package com.example.devdash.model;
import com.example.devdash.helper.SqliteConnection;

import java.sql.*;


/**
 * Singleton model for handling user login and signup operations.
 * Manages database connection and user authentication logic.
 *
 * Author: Alexander Sukhin
 * Version: 04/08/2025
 */
public class LoginModel {

    // Singleton instance of LoginModel
    private static LoginModel instance;
    Connection connection;

    /**
     * Constructor to initialize the database connection.
     */
    public LoginModel() {
        connection = SqliteConnection.Connector();
    }

    /**
     * Returns the singleton instance of LoginModel.
     *
     * @return LoginModel instance
     */
    public static LoginModel getInstance() {
        if (instance == null) {
            instance = new LoginModel();
        }
        return instance;
    }

    /**
     * Checks if the database connection is valid and open.
     *
     * @return True if connected and not closed, false otherwise
     */
    public boolean isDbConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            System.err.println("Error checking DB connection: " + e.getMessage());
            return false;
        }
    }

    /**
     * Attempts to authenticate a user by username and password.
     *
     * @param username The username provided
     * @param password The password provided
     * @return A User object if authentication succeeds, null otherwise
     * @throws SQLException If a database access error occurs
     */
    public User isLogin(String username, String password) throws SQLException {
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            return null;  // invalid input
        }

        String sql = "select * from User WHERE username = ? and password = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username.trim());
            stmt.setString(2, password.trim());

            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    // User found, construct and return User object
                    return new User(
                            resultSet.getString("username"),
                            resultSet.getString("firstName"),
                            resultSet.getString("lastName"),
                            resultSet.getString("accessToken"),
                            resultSet.getInt("id")
                    );
                } else {
                    return null;
                }
            }
        }
    }

    /**
     * Registers a new user in the database.
     *
     * @param username  The username provided
     * @param firstName The first name provided
     * @param lastName  The last name provided
     * @param password  The password provided
     * @return A User object for the user, or null if input invalid
     * @throws SQLException if a database access error occurs
     */
    public User isSignup(String username, String firstName, String lastName, String password) throws SQLException {
        if (username == null || firstName == null || lastName == null || password == null ||
                username.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || password.isEmpty()) {
            return null; // invalid input
        }

        String sql = "INSERT INTO User (username, firstName, lastName, password) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, username);
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setString(4, password);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    // Return new user with generated ID
                    return new User(
                            username,
                            firstName,
                            lastName,
                            null,
                            generatedKeys.getInt(1)
                    );
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }
    }

    /**
     * Creates a default preferences entry for a newly registered user.
     *
     * @param userID The user's ID
     */
    public void createPreferences(int userID) {
        String sql = "INSERT INTO UserPreferences (userId) VALUES (?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, userID);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Checks if a username already exists in the database.
     *
     * @param username Username to check
     * @return True if username exists, false otherwise
     * @throws SQLException If a database access error occurs
     */
    public boolean doesUserExist(String username) throws SQLException {
        String sql = "SELECT 1 FROM User WHERE username = ? LIMIT 1";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username.trim());

            try (ResultSet resultSet = stmt.executeQuery()) {
                return resultSet.next(); // true if exists
            }
        }
    }

    /**
     * Sets the GitHub username within the database
     *
     * @param accessToken GitHub PAT
     * @param userID User's id
     */
    public void setGitHubAccessToken(String accessToken, int userID) {
        String sql = "UPDATE User SET accessToken = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, accessToken);
            stmt.setInt(2, userID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Updates the task on the current status of the task
     *
     * @param taskID ID of the task to update
     * @param completed Current status of the task, true or false
     */
    public void updateTaskCompletion(int taskID, boolean completed) {
        String sql = "UPDATE Task SET completed = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, completed);
            stmt.setInt(2, taskID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
