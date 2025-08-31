package com.example.devdash.model.todo;

import com.example.devdash.helper.data.SqliteConnection;

import java.sql.Connection;
import java.time.LocalDate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SprintModel {
    private final Connection connection;

    public SprintModel() {
        connection = SqliteConnection.Connector();
    }

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
