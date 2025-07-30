package com.example.devdash.controller;

import com.example.devdash.Main;
import com.example.devdash.model.LoginModel;
import com.example.devdash.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SignupController {

    public LoginModel loginModel = new LoginModel();

    @FXML
    private Label isConnected;

    @FXML
    private TextField txtUsername;

    @FXML
    private TextField txtFirstName;

    @FXML
    private TextField txtLastName;

    @FXML
    private TextField txtPassword;

    @FXML
    public void initialize() {
        if (loginModel.isDbConnected()) {
            isConnected.setText("Connected");
        } else {
            isConnected.setText("Not Connected");
        }
    }

    @FXML
    public void Signup() {
        try {

            User user = loginModel.isSignup(txtUsername.getText(), txtFirstName.getText(), txtLastName.getText(), txtPassword.getText());

            if (user != null) {

                // Load Dashboard.fxml manually
                FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/Dashboard.fxml"));
                Parent root = loader.load();

                // Get controller and pass data
                DashboardController dashboardController = loader.getController();
                dashboardController.initializeUser(user);

                // Set the new scene
                Main.getScene().setRoot(root);
            } else {
                isConnected.setText("Username and password are not valid");
            }

        } catch (Exception e) {
            isConnected.setText("Not valid");
            System.out.println(e);
        }
    }

    @FXML
    public void switchToLogin() throws IOException {
        Main.setRoot("LoginPage");
    }

}
