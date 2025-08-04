package com.example.devdash.controller.cards;

import javafx.scene.Node;

/**
 * Interface representing a card component in the dashboard.
 *
 * Author: Alexander Sukhin
 * Version: 04/08/2025
 */
public interface DashboardCard {

    /**
     * Returns the JavaFX Node representing the visual component of the card.
     *
     * @return The UI Node of this dashboard card
     */
    Node getView();
}
