package com.example.devdash.model;

import com.example.devdash.helper.SqliteConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Model sued for handling addition and removal of tasks.
 * Manages logic of Tasks within the database.
 *
 * Author: Alexander Sukhin
 * Version: 04/08/2025
 */
public class TaskModel {

    Connection connection;

    /**
     * Constructor to initialize the task model connection.
     */
    public TaskModel() {
        connection = SqliteConnection.Connector();
    }

    /**
     * Adds a new task for the given user.
     *
     * @param userID ID of the user
     * @param description Task description
     * @return True if insertion succeeds, false otherwise
     */
    public boolean addTask(int userID, String description) {
        String sql = "INSERT INTO Task (userId, description) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            stmt.setString(2, description);

            int affectedRows = stmt.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a task by its ID.
     *
     * @param taskID ID of the task to delete
     * @return True if delete succeeds, false otherwise
     */
    public boolean deleteTask(int taskID) {
        String sql = "DELETE FROM Task WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, taskID);

            int affectedRows = stmt.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all tasks associated with userID
     *
     * @param userID ID of the user
     * @return A list of tasks associated with the user
     */
    public List<Task> getTasksForUser(int userID) {
        String sql = "SELECT * FROM Task WHERE userId = ?";
        List<Task> tasks = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    tasks.add(new Task(
                            resultSet.getInt("id"),
                            resultSet.getString("description"),
                            resultSet.getBoolean("completed")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tasks;
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
