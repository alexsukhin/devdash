package com.example.devdash.controller;

import com.example.devdash.Main;
import com.example.devdash.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.*;

public class DashboardController {

    @FXML private
    GridPane dashboardGrid;
    @FXML
    private CheckMenuItem todoMenuItem;
    @FXML
    private CheckMenuItem pomodoroMenuItem;
    @FXML
    private Label usernameLabel;

    private User user;

    private final Map<String, VBox> cardNodes = new HashMap<>();

    @FXML
    public void initialize() {
        // Setup cards
        cardNodes.put("todo", createCard("Todo List"));
        cardNodes.put("pomodoro", createCard("Pomodoro Timer"));

        // Bind checkboxes to display logic
        todoMenuItem.selectedProperty().addListener((obs, oldVal, newVal) -> updateGrid());
        pomodoroMenuItem.selectedProperty().addListener((obs, oldVal, newVal) -> updateGrid());

        updateGrid(); // initial state
    }

    // You can call this manually after loading the scene
    public void initializeUser(User user) {
        this.user = user;
        usernameLabel.setText(user.getUsername());
    }

    @FXML
    private void switchToLogin() throws IOException {
        Main.setRoot("LoginPage");
    }

    private void updateGrid() {
        dashboardGrid.getChildren().clear();

        List<VBox> selectedCards = new ArrayList<>();
        if (todoMenuItem.isSelected()) selectedCards.add(cardNodes.get("todo"));
        if (pomodoroMenuItem.isSelected()) selectedCards.add(cardNodes.get("pomodoro"));

        int col = 0, row = 0;
        int maxCols = 2;

        for (VBox card : selectedCards) {
            dashboardGrid.add(card, col, row);
            col++;
            if (col >= maxCols) {
                col = 0;
                row++;
            }
        }
    }

    private VBox createCard(String title) {
        VBox card = new VBox();
        card.setStyle("-fx-background-color: lightgray;");
        Label label = new Label(title);
        Button button = new Button("Open");
        card.getChildren().addAll(label, button);
        return card;
    }
}
