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
     * Updates an existing task in the database.
     *
     * @param id          Task's id
     * @param description New task description
     * @param status      New task status
     * @param priority    New priority
     * @param dueDate     New due date (nullable)
     * @return True if the update succeeds, false otherwise
     */
    public boolean updateTask(int id, String description, String status, int priority, String dueDate) {
        String sql = "UPDATE Task SET description = ?, status = ?, priority = ?, dueDate = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, description);
            stmt.setString(2, status);
            stmt.setInt(3, priority);
            stmt.setString(4, dueDate);
            stmt.setInt(5, id);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates the updatedAt timestamp of a task to the current time.
     *
     * @param taskID ID of the task to update
     * @return True if the update succeeds, false otherwise
     */
    public boolean updateTaskTimestamp(int taskID) {
        String sql = "UPDATE Task SET updatedAt = datetime('now','localtime') WHERE id = ?";
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
     * Updates the status of a task.
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
     * Assigns a task to a sprint.
     */
    public boolean assignTaskToSprint(int taskID, int sprintID) {
        String sql = "UPDATE Task SET sprintId = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, sprintID);
            stmt.setInt(2, taskID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Removes a task from its sprint (moves it back to backlog).
     */
    public boolean removeTaskFromSprint(int taskID) {
        String sql = "UPDATE Task SET sprintId = NULL WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, taskID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets all tasks for a given sprint.
     */
    public List<Task> getTasksForSprint(int sprintID) {
        String sql = "SELECT * FROM Task WHERE sprintId = ? ORDER BY priority DESC";
        List<Task> tasks = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, sprintID);
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    tasks.add(new Task(
                            resultSet.getInt("id"),
                            resultSet.getString("description"),
                            resultSet.getString("status"),
                            resultSet.getInt("priority"),
                            resultSet.getString("dueDate"),
                            resultSet.getString("updatedAt")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tasks;
    }

    /**
     * Gets all backlog tasks (not in any sprint).
     */
    public List<Task> getBacklogTasks(int userID) {
        String sql = "SELECT * FROM Task WHERE userId = ? AND sprintId IS NULL ORDER BY priority DESC";
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
                            resultSet.getString("updatedAt")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tasks;
    }

}
