package com.example.devdash.controller.cards.github;

import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.util.Duration;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;

/**
 * Manages a GitHub-style commit heatmap for a full year.
 *
 * Author: Alexander Sukhin
 * Version: 25/08/2025
 */
public class CommitHeatmap {

    private GridPane grid;

    /**
     * Constructor for CommitHeatmap.
     *
     * @param grid GridPane where heatmap cells will be added
     */
    public CommitHeatmap(GridPane grid) {
        this.grid = grid;
    }


    /**
     * Populates the heatmap for a full year based on daily commit counts.
     *
     * @param dailyCounts Map of LocalDate to number of commits
     * @param year        The year to populate
     */
    public void populateHeatMapFullYear(Map<LocalDate, Integer> dailyCounts, int year) {
        grid.getChildren().clear();

        LocalDate firstDay = LocalDate.of(year,1,1);
        LocalDate lastDay = LocalDate.of(year,12,31);

        while (firstDay.getDayOfWeek() != DayOfWeek.SUNDAY) {
            firstDay = firstDay.minusDays(1);
        }

        long totalDays = ChronoUnit.DAYS.between(firstDay, lastDay) + 1;
        for (int i=0; i<totalDays; i++) {
            LocalDate date = firstDay.plusDays(i);
            int count = dailyCounts.getOrDefault(date,0);

            Region cell = new Region();
            cell.getStyleClass().add("cell-background");

            if (count > 0) cell.setStyle("-fx-background-color: " + getColor(count) + ";");

            Tooltip tooltip = new Tooltip(date + "\nCommits: " + count);
            tooltip.setShowDelay(Duration.millis(100));
            Tooltip.install(cell, tooltip);

            int week = i/7;
            int dayOfWeek = date.getDayOfWeek().getValue()%7;
            grid.add(cell, week, dayOfWeek);
        }
    }


    /**
     * Returns a color for a cell based on the number of commits.
     *
     * @param count Number of commits on a day
     * @return Hex color string
     */
    private String getColor(int count) {
        if (count <= 0) return "transparent";
        if (count == 1) return "#9be9a8";
        if (count == 2) return "#40c463";
        if (count == 3) return "#30a14e";
        return "#216e39";
    }


    /**
     * Clears the heatmap from the GridPane.
     */
    public void clear() {
        grid.getChildren().clear();
    }
}