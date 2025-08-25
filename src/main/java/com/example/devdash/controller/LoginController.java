package com.example.devdash.controller;

import com.example.devdash.Main;
import com.example.devdash.helper.data.Session;
import com.example.devdash.model.auth.LoginModel;
import com.example.devdash.model.auth.User;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
     * Controller for the login view.
     * Handles login logic and form validation.
     *
     * @author Alexander Sukhin
     * @version 04/08/2025
 */
public class LoginController {

    // Singleton instance of LoginModel that handles authentication and DB connection
    private final LoginModel loginModel = LoginModel.getInstance();

    @FXML private Label isConnected;
    @FXML private TextField txtUsername;
    @FXML private TextField txtPassword;

    /**
     * Called automatically after the FXML file is loaded.
     * Used to check and display the database connection status.
     */
    @FXML
    public void initialize() {
        if (loginModel.isDbConnected()) {
            isConnected.setText("Connected");
        } else {
            isConnected.setText("Not Connected");
        }
    }

    /**
     * Handles the login button click.
     * Validates input, performs login check, and loads the dashboard if successful.
     */
    @FXML
    public void login() {
        try {
            // Ensures both username and password fields are filled
            if (txtUsername.getText().isEmpty() || txtPassword.getText().isEmpty()) {
                isConnected.setText("Please enter both username and password");
                return;
            }

            User user = loginModel.isLogin(txtUsername.getText(), txtPassword.getText());

            if (user != null) {
                // Successful login: load dashboard
                Session.getInstance().setUser(user);

                FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/main/Dashboard.fxml"));
                Parent root = loader.load();

                Main.getScene().setRoot(root);
            } else {
                // Failed login: inform user and clear password
                isConnected.setText("Username and password are not correct");
                txtPassword.clear();
            }
        } catch (IOException e) {
            isConnected.setText("Error loading dashboard.");
            e.printStackTrace();
        } catch (Exception e) {
            isConnected.setText("Unexpected error occurred.");
            e.printStackTrace();
        }
    }

    /**
     * Switches the scene to the signup page.
     * Called when the user clicks on "Sign up" link or button.
     *
     * @throws IOException If loading the FXML fails.
     */
    @FXML
    public void switchToSignup() throws IOException {
        Main.setRoot("SignupPage");
    }

}
