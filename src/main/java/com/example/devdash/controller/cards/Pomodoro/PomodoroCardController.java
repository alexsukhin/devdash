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

public class PomodoroCardController implements DashboardCard, PomodoroSwitchHandler {

    @FXML private VBox rootVBox;
    @FXML private RadioButton focusButton;
    @FXML private RadioButton breakButton;
    @FXML private StackPane contentPane;
    @FXML private ToggleGroup paneToggleGroup;

    private Map<Toggle, Node> tabViews;
    private Map<Toggle, PomodoroPaneController> tabControllers;
    private Toggle currentToggle;

    @FXML
    public void initialize() throws IOException {


        // https://stackoverflow.com/questions/71157873/fill-all-the-tabpane-width-with-tabs-in-javafx

        focusButton.getStyleClass().remove("radio-button");
        breakButton.getStyleClass().remove("radio-button");

        FXMLUtils focusLoaded = FXMLUtils.loadFXML("FocusPomodoro");
        FXMLUtils breakLoaded = FXMLUtils.loadFXML("BreakPomodoro");

        if (focusLoaded == null || breakLoaded == null) {
            throw new IOException("Failed to load required Pomodoro FXML files");
        }

        Node focusContentRoot = focusLoaded.getRoot();
        Node breakContentRoot = breakLoaded.getRoot();

        FocusPomodoroController focusController = (FocusPomodoroController) focusLoaded.getController();
        BreakPomodoroController breakController = (BreakPomodoroController) breakLoaded.getController();

        focusController.setSwitchHandler(this);
        breakController.setSwitchHandler(this);

        tabViews = Map.of(
                focusButton, focusContentRoot,
                breakButton, breakContentRoot
        );

        tabControllers = Map.of(
                focusButton, focusController,
                breakButton, breakController
        );

        focusButton.setSelected(true);

        displaySelectedPane();
        paneToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) ->
                displaySelectedPane()
        );
    }

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

    @Override
    public void switchToBreak() {
        breakButton.setSelected(true);
        displaySelectedPane();
    }

    @Override
    public void switchToFocus() {
        focusButton.setSelected(true);
        displaySelectedPane();
    }

    public Node getView() {
        return rootVBox;
    }
}
