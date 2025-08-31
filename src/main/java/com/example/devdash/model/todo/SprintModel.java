package com.example.devdash.model.todo;

import com.example.devdash.helper.data.SqliteConnection;

import java.sql.Connection;
import java.time.LocalDate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object (DAO) for managing Sprint entities in the database.
 * Provides CRUD operations related to sprints, including creation,
 * retrieval, and deletion.
 *
 * Author: Alexander Sukhin
 * Version: 31/08/2025
 */
public class SprintModel {
    private final Connection connection;

    /**
     * Constructor to initialize the task model connection.
     */
    public SprintModel() {
        connection = SqliteConnection.Connector();
    }


    /**
     * Adds a new sprint for a specific user.
     *
     * @param userId    The user's ID
     * @param name      the name of the sprint
     * @param startDate the start date of the sprint
     * @param endDate   the end date of the sprint
     * @return True if the sprint was successfully inserted into the database, false otherwise
     */
    public boolean addSprint(int userId, String name, LocalDate startDate, LocalDate endDate) {
        String sql = "INSERT INTO Sprint (userId, name, startDate, endDate) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, name);
            stmt.setString(3, startDate.toString());
            stmt.setString(4, endDate.toString());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all sprints associated with a given user.
     *
     * @param userId the user ID
     * @return a list of Sprint objects belonging to the specified user
     */
    public List<Sprint> getSprintsForUser(int userId) {
        List<Sprint> sprints = new ArrayList<>();
        String sql = "SELECT * FROM Sprint WHERE userId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                sprints.add(new Sprint(
                        rs.getInt("id"),
                        rs.getInt("userId"),
                        rs.getString("name"),
                        LocalDate.parse(rs.getString("startDate")),
                        LocalDate.parse(rs.getString("endDate"))
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sprints;
    }

    /**
     * Deletes a sprint from the database.
     *
     * @param sprintId the ID of the sprint to delete
     * @return True if the sprint was successfully deleted, false otherwise
     */
    public boolean deleteSprint(int sprintId) {
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM Sprint WHERE id = ?")) {
            stmt.setInt(1, sprintId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
