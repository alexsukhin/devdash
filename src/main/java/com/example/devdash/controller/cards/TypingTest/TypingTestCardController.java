package com.example.devdash.controller.cards.TypingTest;

import com.example.devdash.controller.cards.DashboardCard;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the Typing Test card in the dashboard.
 *
 * Author: Alexander Sukhin
 * Version: 04/08/2025
 */
public class TypingTestCardController implements DashboardCard {

    @FXML private Node rootNode;
    @FXML private StackPane stackPane;  // fx:id from SceneBuilder
    @FXML private TextField textField;  // fx:id from SceneBuilder


    /**
     * Handler for the Start button click event.
     */
    @FXML
    public void initialize() {
    }


    /**
     * Returns the root UI node for this card.
     *
     * @return The root node of this card
     */
    public Node getView() {
        return rootNode;
    }
}
