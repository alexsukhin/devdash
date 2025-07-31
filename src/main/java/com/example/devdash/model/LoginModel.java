    package com.example.devdash.model;
    import java.sql.*;

    public class LoginModel {

        // Singleton model
        private static final LoginModel INSTANCE = new LoginModel();
        Connection connection;

        public LoginModel() {
            connection = SqliteConnection.Connector();
            if (connection == null)  {
                throw new IllegalStateException("Failed to connect to database");
            }
        }

        public static LoginModel getInstance() {
            return INSTANCE;
        }

        public boolean isDbConnected() {
            try {
                return connection != null && !connection.isClosed();
            } catch (SQLException e) {
                System.err.println("Error checking DB connection: " + e.getMessage());
                return false;
            }
        }

        public User isLogin(String username, String password) throws SQLException {
            if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
                return null;
            }

            String query = "select * from User WHERE username = ? and password = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, username.trim());
                stmt.setString(2, password.trim());

                try (ResultSet resultSet = stmt.executeQuery()) {
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
        }

        public User isSignup(String username, String firstName, String lastName, String password) throws SQLException {
            if (username == null || firstName == null || lastName == null || password == null ||
                    username.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || password.isEmpty()) {
                return null;
            }

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

        public boolean doesUserExist(String username) throws SQLException {
            String query = "SELECT 1 FROM User WHERE username = ? LIMIT 1";

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, username.trim());

                try (ResultSet resultSet = stmt.executeQuery()) {
                    return resultSet.next();
                }
            }
        }


    }
