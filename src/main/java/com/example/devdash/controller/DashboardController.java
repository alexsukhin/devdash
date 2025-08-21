package com.example.devdash.controller;

import com.example.devdash.Main;
import com.example.devdash.controller.cards.DashboardCard;
import com.example.devdash.helper.FXMLUtils;
import com.example.devdash.helper.Session;
import com.example.devdash.helper.Span;
import com.example.devdash.helper.Theme;
import com.example.devdash.model.PreferencesModel;
import com.example.devdash.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.GridPane;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * Controller for the Dashboard view.
 * Manages dynamic loading and layout of dashboard cards,
 * user display, and customization of visible cards.
 *
 * Author: Alexander Sukhin
 * Version: 04/08/2025
 */
public class DashboardController {

    @FXML private GridPane dashboardGrid;         // The GridPane layout to place dashboard cards in
    @FXML private Label usernameLabel;            // Label showing the logged-in user's username
    @FXML private MenuButton customizeMenuButton; // MenuButton for toggling card visibility
    @FXML private FontIcon themeIcon;

    private User user;
    private PreferencesModel preferencesModel;

    // Map card keys to their corresponding FXML filenames
    private static final Map<String, String> CARD_FXML_MAP = Map.of(
            "pomodoro", "PomodoroCard",
            "todo", "ToDoCard",
            "typing-test", "TypingTestCard",
            "git-hub", "GitHubCard"

    );

    private final Map<String, DashboardCard> cardNodes = new HashMap<>();
    private final Map<CheckMenuItem, String> menuItemToCardKey = new HashMap<>();

    /**
     * Called automatically after the FXML file is loaded.
     * Loads all dashboard cards, creates toggle menu items
     * for each card, and initially displays all cards.
     *
     * @throws IOException If loading any FXML file fails
     */
    @FXML
    public void initialize() throws IOException, SQLException {

        user = Session.getInstance().getUser();
        preferencesModel = PreferencesModel.getInstance();

        usernameLabel.setText(user.getUsername());

        // Switches theme to dark if set as user's preferences
        boolean isDark = preferencesModel.getDarkMode(user.getID());
        if (isDark) switchTheme(isDark);

        for (Map.Entry<String, String> entry : CARD_FXML_MAP.entrySet()) {
            // Load FXML and get controller for each card
            FXMLUtils loaded = FXMLUtils.loadFXML(entry.getValue());
            Object controller = loaded != null ? loaded.getController() : null;

            if (controller instanceof DashboardCard card) {
                cardNodes.put(entry.getKey(), card);

                // Create a CheckMenuItem for toggling card
                CheckMenuItem menuItem = new CheckMenuItem(entry.getKey());
                menuItem.setSelected(true);

                // When toggled, update the grid layout
                menuItem.selectedProperty().addListener((obs, oldVal, newVal) -> updateGrid());

                customizeMenuButton.getItems().add(menuItem);
                menuItemToCardKey.put(menuItem, entry.getKey());
            }
        }

        updateGrid();
    }

    /**
     * Updates the dashboardGrid layout based on which cards are toggled on.
     */
    private void updateGrid() {
        dashboardGrid.getChildren().clear();

        // Collect alls currently selected cards
        List<DashboardCard> selectedCards = new ArrayList<>();
        for (Map.Entry<CheckMenuItem, String> entry : menuItemToCardKey.entrySet()) {
            if (entry.getKey().isSelected()) {
                DashboardCard card = cardNodes.get(entry.getValue());
                if (card != null) selectedCards.add(card);
            }
        }

        int numCards = selectedCards.size();

        // Add each selected card to the grid with layout determined by Span
        for (int i = 0; i < numCards; i++) {
            DashboardCard card = selectedCards.get(i);

            Span span = Span.forCard(numCards, i);
            dashboardGrid.add(card.getView(), span.col, span.row, span.colSpan, span.rowSpan);
        }
    }

    @FXML
    public void updateTheme() {
        int userId = user.getID();
        boolean isDark = preferencesModel.getDarkMode(userId);
        boolean newValue = !isDark;

        preferencesModel.updateDarkMode(userId, newValue);

        switchTheme(newValue);

    }

    public void switchTheme(boolean isDark) {
        if (isDark) {
            Main.changeTheme(Theme.DARK);
            themeIcon.setIconLiteral("fa-moon-o");
        } else {
            Main.changeTheme(Theme.LIGHT);
            themeIcon.setIconLiteral("fa-sun-o");
        }
    }

    /**
     * Switches the current scene back to the login page.
     *
     * @throws IOException if loading the LoginPage FXML fails
     */
    @FXML
    private void switchToLogin() throws IOException {
        Session.getInstance().clear();
        Main.setRoot("LoginPage");
    }
}
