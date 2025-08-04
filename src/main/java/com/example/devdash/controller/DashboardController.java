package com.example.devdash.controller;

import com.example.devdash.Main;
import com.example.devdash.controller.cards.DashboardCard;
import com.example.devdash.helper.FXMLUtils;
import com.example.devdash.helper.Span;
import com.example.devdash.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.*;

public class DashboardController {

    @FXML private GridPane dashboardGrid;
    @FXML private Label usernameLabel;
    @FXML private MenuButton customizeMenuButton;

    private static final Map<String, String> CARD_FXML_MAP = Map.of(
            "pomodoro", "PomodoroCard",
            "todo", "ToDoCard",
            "typing-test", "TypingTestCard",
            "git-hub", "GitHubCard"

    );

    private final Map<String, DashboardCard> cardNodes = new HashMap<>();
    private final Map<CheckMenuItem, String> menuItemToCardKey = new HashMap<>();

    private User user;

    public void initializeUser(User user) {
        this.user = user;
        usernameLabel.setText(user.getUsername());
    }

    @FXML
    public void initialize() throws IOException {

        for (Map.Entry<String, String> entry : CARD_FXML_MAP.entrySet()) {
            FXMLUtils loaded = FXMLUtils.loadFXML(entry.getValue());

            Object controller = loaded != null ? loaded.getController() : null;

            if (controller instanceof DashboardCard card) {
                cardNodes.put(entry.getKey(), card);

                CheckMenuItem menuItem = new CheckMenuItem(entry.getKey());
                menuItem.setSelected(true);

                menuItem.selectedProperty().addListener((obs, oldVal, newVal) -> updateGrid());

                customizeMenuButton.getItems().add(menuItem);
                menuItemToCardKey.put(menuItem, entry.getKey());
            }
        }

        updateGrid();
    }

    private void updateGrid() {
        dashboardGrid.getChildren().clear();

        List<DashboardCard> selectedCards = new ArrayList<>();

        for (Map.Entry<CheckMenuItem, String> entry : menuItemToCardKey.entrySet()) {
            if (entry.getKey().isSelected()) {
                DashboardCard card = cardNodes.get(entry.getValue());
                if (card != null) selectedCards.add(card);
            }
        }

        int numCards = selectedCards.size();

        for (int i = 0; i < numCards; i++) {
            DashboardCard card = selectedCards.get(i);

            Span span = Span.forCard(numCards, i);
            dashboardGrid.add(card.getView(), span.col, span.row, span.colSpan, span.rowSpan);
        }
    }

    @FXML
    private void switchToLogin() throws IOException {
        Main.setRoot("LoginPage");
    }

}
