package com.example.devdash.controller.cards;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class GitHubCardController implements DashboardCard {

    @FXML private VBox rootVBox;
    @FXML private Label titleLabel;
    @FXML private Button startButton;
    private Node view;

    @FXML
    public void handleStart() {
        titleLabel.setText("GitHub Card");
        startButton.setText("Start");
    }

    public Node getView() {
        return rootVBox;
    }

    public void refresh() {
        // Refresh card
    }
}
