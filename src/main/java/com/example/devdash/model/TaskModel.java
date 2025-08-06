package com.example.devdash.model;

import com.example.devdash.helper.SqliteConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskModel {

    Connection connection;

    /**
     * Constructor to initialize the task model connection.
     */
    public TaskModel() {
        connection = SqliteConnection.Connector();
        if (connection == null)  {
            throw new IllegalStateException("Failed to connect to database");
        }
    }

    /**
     * Adds a new task for the given user.
     *
     * @param userId ID of the user
     * @param description Task description
     * @return true if insert succeeded, false otherwise
     */
    public boolean addTask(int userId, String description) {
        String sql = "INSERT INTO Task (userId, description) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, description);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a task by its ID.
     *
     * @param taskId ID of the task to delete
     * @return true if delete succeeded, false otherwise
     */
    public boolean deleteTask(int taskId) {
        String sql = "DELETE FROM Task WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, taskId);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Task> getTasksForUser(int userId) {
        String query = "SELECT * FROM Task WHERE userId = ?";
        List<Task> tasks = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(new Task(
                            rs.getInt("id"),
                            rs.getString("description"),
                            rs.getBoolean("completed")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tasks;
    }

    public boolean updateTaskCompletion(int taskId, boolean completed) {
        String sql = "UPDATE Task SET completed = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, completed);
            stmt.setInt(2, taskId);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
