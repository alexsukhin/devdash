package com.example.devdash.controller.cards.Pomodoro;

import com.example.devdash.controller.cards.DashboardCard;
import com.example.devdash.helper.FXMLUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Map;

/**
 * Controller for the Pomodoro card displayed on the dashboard.
 *
 * Author: Alexander Sukhin
 * Date: 04/08/2025
 */
public class PomodoroCardController implements DashboardCard, PomodoroSwitchHandler {

    @FXML private VBox rootVBox;
    @FXML private RadioButton focusButton;
    @FXML private RadioButton breakButton;
    @FXML private StackPane contentPane;
    @FXML private ToggleGroup paneToggleGroup;

    private Map<Toggle, Node> tabViews;
    private Map<Toggle, PomodoroPaneController> tabControllers;
    private Toggle currentToggle;

    /**
     * Called automatically after the FXML file is loaded.
     * Initializes the controller by loading Focus and Break panes.
     */
    @FXML
    public void initialize() throws IOException {


        // https://stackoverflow.com/questions/71157873/fill-all-the-tabpane-width-with-tabs-in-javafx

        // Removes default radio button styling
        focusButton.getStyleClass().remove("radio-button");
        breakButton.getStyleClass().remove("radio-button");

        // Load FXML files for Focus and Break modes
        FXMLUtils focusLoaded = FXMLUtils.loadFXML("FocusPomodoro");
        FXMLUtils breakLoaded = FXMLUtils.loadFXML("BreakPomodoro");

        if (focusLoaded == null || breakLoaded == null) {
            throw new IOException("Failed to load required Pomodoro FXML files");
        }

        // Extract roots and controllers
        Node focusContentRoot = focusLoaded.getRoot();
        Node breakContentRoot = breakLoaded.getRoot();

        FocusPomodoroController focusController = (FocusPomodoroController) focusLoaded.getController();
        BreakPomodoroController breakController = (BreakPomodoroController) breakLoaded.getController();

        // Set the switch handler so each controller can trigger mode switches
        focusController.setSwitchHandler(this);
        breakController.setSwitchHandler(this);

        // Map toggles to content and controllers
        tabViews = Map.of(
                focusButton, focusContentRoot,
                breakButton, breakContentRoot
        );

        tabControllers = Map.of(
                focusButton, focusController,
                breakButton, breakController
        );

        // Set default selection and bind toggle behavior
        focusButton.setSelected(true);
        displaySelectedPane();

        paneToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) ->
                displaySelectedPane()
        );
    }

    /**
     * Displays the selected Pomodoro pane,
     * and resets the previously active pane.
     */
    private void displaySelectedPane() {
        Toggle selectedToggle = paneToggleGroup.getSelectedToggle();

        if (selectedToggle != null) {
            if (currentToggle != null) {

                PomodoroPaneController controller = tabControllers.get(currentToggle);
                if (controller != null) {
                    controller.reset();
                }
            }

            Node node = tabViews.get(selectedToggle);
            if (node != null) {
                contentPane.getChildren().setAll(node);
            }

            currentToggle = selectedToggle;
        } else {
            contentPane.getChildren().clear();
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
     * @return The root VBox node of this card
     */
    public Node getView() {
        return rootVBox;
    }
}
