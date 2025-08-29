package com.example.devdash.model.todo;

import com.example.devdash.helper.data.SqliteConnection;

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
     * @param userID      ID of the user
     * @param description Task description
     * @param status
     * @return True if insertion succeeds, false otherwise
     */
    public boolean addTask(int userID, String description, String status, int priority, String dueDate) {
        String sql = "INSERT INTO Task (userId, description, status , priority, dueDate) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            stmt.setString(2, description);
            stmt.setString(3, status);
            stmt.setInt(4, priority);
            stmt.setString(5, dueDate);

            return stmt.executeUpdate() > 0;
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
        String sql = "SELECT * FROM Task WHERE userId = ? ORDER BY priority DESC";
        List<Task> tasks = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    tasks.add(new Task(
                            resultSet.getInt("id"),
                            resultSet.getString("description"),
                            resultSet.getString("status"),
                            resultSet.getInt("priority"),
                            resultSet.getString("dueDate"),
                            resultSet.getInt("position")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tasks;
    }

    /**
     * Updates the status of a task (e.g., TODO, IN_PROGRESS, DONE).
     *
     * @param taskID ID of the task to update
     * @param status New status of the task
     */
    public void updateTaskStatus(int taskID, String status) {
        String sql = "UPDATE Task SET status = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, taskID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the position of a task within its column (for drag-and-drop ordering).
     *
     * @param taskID   ID of the task to update
     * @param position New position of the task
     */
    public void updateTaskPosition(int taskID, int position) {
        String sql = "UPDATE Task SET position = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, position);
            stmt.setInt(2, taskID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
