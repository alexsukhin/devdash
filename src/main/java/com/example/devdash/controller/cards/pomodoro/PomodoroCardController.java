package com.example.devdash.controller.cards.pomodoro;

import com.example.devdash.controller.cards.DashboardCard;
import com.example.devdash.helper.ui.FXMLUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for the Pomodoro card displayed on the dashboard.
 *
 * Author: Alexander Sukhin
 * Date: 04/08/2025
 */
public class PomodoroCardController implements DashboardCard {

    @FXML private VBox rootNode;
    @FXML private StackPane contentPane;
    @FXML private ToggleButton focusButton;
    @FXML private ToggleButton breakButton;
    @FXML private ToggleGroup togglePane;

    private final Map<ToggleButton, Node> buttonViews = new HashMap<>();
    private final Map<ToggleButton, PomodoroPaneController> buttonControllers = new HashMap<>();
    private ToggleButton currentButton;



    /**
     * Called automatically after the FXML file is loaded.
     * Initializes the controller by loading Focus and Break panes.
     *
     * @throws IOException if the required FXML files fail to load
     */
    @FXML
    public void initialize() throws IOException {
        loadPane(focusButton, "pomodoro/FocusPomodoro");
        loadPane(breakButton, "pomodoro/BreakPomodoro");

        togglePane.selectToggle(focusButton);
        displayPane(focusButton);

        focusButton.setOnAction(e -> displayPane(focusButton));
        breakButton.setOnAction(e -> displayPane(breakButton));
    }



    /**
     * Loads the FXML file for a Pomodoro pane.
     *
     * @param button The Button toggle associated with this pane
     * @param fxmlName The name of the FXML file
     * @throws IOException If loading the FXML file fails
     */
    private void loadPane(ToggleButton button, String fxmlName) throws IOException {
        FXMLUtils loaded = FXMLUtils.loadFXML(fxmlName);
        Node root = loaded.getRoot();
        Object controller = loaded.getController();

        if (!(controller instanceof PomodoroPaneController paneController)) {
            throw new IllegalStateException(fxmlName + " must implement PomodoroPaneController");
        }

        paneController.setCardController(this);

        buttonViews.put(button, root);
        buttonControllers.put(button, paneController);
    }

    /**
     * Displays the Pomodoro pane,
     * and resets the previously active pane.
     */
    private void displayPane(ToggleButton button) {
        // Reset previous pane if any
        if (currentButton != null) {
            PomodoroPaneController prevController = buttonControllers.get(currentButton);
            if (prevController != null) prevController.resetTime();
        }

        // Show new pane
        contentPane.getChildren().setAll(buttonViews.get(button));
        buttonControllers.get(button).resetTime();
        currentButton = button;
    }


    /**
     * Switches the Pomodoro pane to Focus mode.
     */
    public void switchToFocus() {
        displayPane(focusButton);
    }

    /**
     * Switches the Pomodoro pane to Break mode.
     */
    public void switchToBreak() {
        displayPane(breakButton);
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
