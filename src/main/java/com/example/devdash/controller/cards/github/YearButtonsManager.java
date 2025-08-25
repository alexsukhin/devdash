package com.example.devdash.controller.cards.github;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import java.time.LocalDate;

/**
 * Manages year selection buttons for GitHub commit heatmap.
 *
 * Author: Alexander Sukhin
 * Version: 25/08/2025
 */
public class YearButtonsManager {

    private VBox container;
    private GitHubCardController controller;

    /**
     * Constructor for YearButtonsManager.
     *
     * @param container  VBox where year buttons will be added
     * @param controller GitHubCardController that handles heatmap updates
     */
    public YearButtonsManager(VBox container, GitHubCardController controller) {
        this.container = container;
        this.controller = controller;
    }

    /**
     * Populates the container with buttons for the current year and the past 3 years.
     * Clicking a button updates the heatmap for the selected year.
     */
    public void populateYearButtons() {
        container.getChildren().clear();
        int currentYear = LocalDate.now().getYear();
        for (int year = currentYear; year >= currentYear - 3; year--) {
            Button btn = new Button(String.valueOf(year));
            btn.getStyleClass().add("button-theme");
            btn.setPrefWidth(40);
            btn.setMinWidth(40);
            btn.setMaxWidth(50);
            btn.setPrefHeight(25);
            btn.setStyle("-fx-font-size: 10px;");
            int finalYear = year;
            btn.setOnAction(e -> controller.populateHeatMapForYear(finalYear));
            container.getChildren().add(btn);
        }
    }

    /**
     * Clears all year buttons from the container.
     */
    public void clear() {
        container.getChildren().clear();
    }
}