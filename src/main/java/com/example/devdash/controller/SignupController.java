package com.example.devdash.controller;

import com.example.devdash.Main;
import com.example.devdash.helper.Session;
import com.example.devdash.model.LoginModel;
import com.example.devdash.model.User;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * Controller for the signup view.
 * Handles signup logic and form validation.
 *
 * @author Alexander Sukhin
 * @version 04/08/2025
 */
public class SignupController {

    // Singleton instance of LoginModel that handles authentication and DB connection
    private final LoginModel loginModel = LoginModel.getInstance();

    @FXML private Label isConnected;
    @FXML private TextField txtUsername;
    @FXML private TextField txtFirstName;
    @FXML private TextField txtLastName;
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
     * Handles the signup button click.
     * Validates signup, performs signup check, and loads the dashboard if successful.
     */
    @FXML
    public void signup() {
        try {

            // Check that all input fields are filled in
            if (txtUsername.getText().isEmpty() || txtFirstName.getText().isEmpty() || txtLastName.getText().isEmpty() ||txtPassword.getText().isEmpty()) {
                isConnected.setText("Please fill in all fields.");
                return;
            }

            // Prevent duplicate usernames
            if (loginModel.doesUserExist(txtUsername.getText())) {
                isConnected.setText("Username already exists.");
                return;
            }

            User user = loginModel.isSignup(txtUsername.getText(), txtFirstName.getText(), txtLastName.getText(), txtPassword.getText());

            if (user != null) {
                // Successful signup: load dashboard
                Session.getInstance().setUser(user);

                FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/Dashboard.fxml"));
                Parent root = loader.load();

                Main.getScene().setRoot(root);
            } else {
                // Failed signup: inform user
                isConnected.setText("Signup failed. Try again.");
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
    public void switchToLogin() throws IOException {
        Main.setRoot("LoginPage");
    }

}
