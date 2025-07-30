    package com.example.devdash.model;
    import java.awt.color.ICC_ColorSpace;
    import java.sql.*;

    public class LoginModel {
        Connection connection;

        public LoginModel() {
            connection = SqliteConnection.Connector();
            if (connection == null)  {
                System.out.println("Connection is null");
                System.exit(1);
            }
        }

        public boolean isDbConnected() {
            try {
                return !connection.isClosed();
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        public User isLogin(String username, String password) throws SQLException {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            String query = "select * from User WHERE username = ? and password = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                resultSet = stmt.executeQuery();

                if (resultSet.next()) {
                    return new User(
                            resultSet.getString("username"),
                            resultSet.getString("firstName"),
                            resultSet.getString("lastName"),
                            resultSet.getInt("id")
                    );
                } else {
                    return null;
                }
            }
        }

        public User isSignup(String username, String firstName, String lastName, String password) throws SQLException {
            PreparedStatement preparedStatement = null;

            String query = "INSERT INTO User (username, firstName, lastName, password) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
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
                        return new User(
                                username,
                                firstName,
                                lastName,
                                generatedKeys.getInt(1)
                        );
                    } else {
                        throw new SQLException("Creating user failed, no ID obtained.");
                    }
                }
            }
        }

    }
