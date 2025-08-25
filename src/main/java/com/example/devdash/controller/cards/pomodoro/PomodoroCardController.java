package com.example.devdash.controller.cards.pomodoro;

import com.example.devdash.controller.cards.DashboardCard;
import com.example.devdash.helper.ui.FXMLUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for the Pomodoro card displayed on the dashboard.
 *
 * Author: Alexander Sukhin
 * Date: 04/08/2025
 */
public class PomodoroCardController implements DashboardCard, PomodoroSwitchHandler {

    @FXML private Node rootNode;
    @FXML private RadioButton focusButton;
    @FXML private RadioButton breakButton;
    @FXML private StackPane contentPane;
    @FXML private ToggleGroup paneToggleGroup;

    private Map<Toggle, Node> tabViews = new HashMap<>();
    private Map<Toggle, PomodoroPaneController> tabControllers = new HashMap<>();
    private Toggle currentToggle;

    /**
     * Called automatically after the FXML file is loaded.
     * Initializes the controller by loading Focus and Break panes.
     *
     * @throws IOException if the required FXML files fail to load
     */
    @FXML
    public void initialize() throws IOException {


        // https://stackoverflow.com/questions/71157873/fill-all-the-tabpane-width-with-tabs-in-javafx

        // Removes default radio button styling
        focusButton.getStyleClass().remove("radio-button");
        breakButton.getStyleClass().remove("radio-button");

        // Load each Pomodoro pane and controller from FXML
        loadPane(focusButton, "pomodoro/FocusPomodoro");
        loadPane(breakButton, "pomodoro/BreakPomodoro");

        // Selects the Focus Pane as default
        focusButton.setSelected(true);
        displaySelectedPane();

        paneToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) ->
                {
                    displaySelectedPane();
                }
        );
    }

    /**
     * Loads the FXML file for a Pomodoro pane.
     *
     * @param toggle The RadioButton toggle associated with this pane
     * @param fxmlName The name of the FXML file
     * @throws IOException If loading the FXML file fails
     */
    private void loadPane(RadioButton toggle, String fxmlName) throws IOException {
        FXMLUtils loaded = FXMLUtils.loadFXML(fxmlName);

        if (loaded == null) {
            throw new IOException("Failed to load " + fxmlName + ".fxml");
        }

        Node root = loaded.getRoot();
        Object controller = loaded.getController();

        if (!(controller instanceof PomodoroPaneController paneController)) {
            throw new IllegalStateException(fxmlName + " controller must implement PomodoroPaneController");
        }

        paneController.setSwitchHandler(this);

        tabViews.put(toggle, root);
        tabControllers.put(toggle, paneController);
    }

    /**
     * Displays the selected Pomodoro pane,
     * and resets the previously active pane.
     */
    private void displaySelectedPane() {
        Toggle selectedToggle = paneToggleGroup.getSelectedToggle();

        if (selectedToggle == null) {
            contentPane.getChildren().clear();
            return;
        }

        resetPreviousController();
        showNewPane(selectedToggle);
        currentToggle = selectedToggle;
    }

    /**
     * Resets the controller of the previously selected toggle (if any).
     */
    private void resetPreviousController(){
        if (currentToggle != null) {
            PomodoroPaneController previousController = tabControllers.get(currentToggle);
            if (previousController != null) {
                previousController.resetTime();
            }
        }
    }

    /**
    * Displays the Node associated with the selected toggle.
    */
    private void showNewPane(Toggle selectedToggle) {
        Node node = tabViews.get(selectedToggle);
        if (node != null) {
            contentPane.getChildren().setAll(node);
        }
    }

    /**
     * Switches the Pomodoro pane to Break mode.
     */
    @Override
    public void switchToBreak() {
        breakButton.setSelected(true);
        displaySelectedPane();
    }

    /**
     * Switches the Pomodoro pane to Focus mode.
     */
    @Override
    public void switchToFocus() {
        focusButton.setSelected(true);
        displaySelectedPane();
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
