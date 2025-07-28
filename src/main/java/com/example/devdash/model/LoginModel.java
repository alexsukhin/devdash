    package com.example.devdash.model;
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
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return new User(
                            rs.getString("username"),
                            rs.getInt("id")
                    );
                } else {
                    return null;
                }
            }
        }

    }
