package com.example.devdash.controller.cards.Pomodoro;

import com.example.devdash.controller.cards.DashboardCard;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import com.example.devdash.Main;

import java.io.IOException;
import java.util.Map;

public class PomodoroCardController implements DashboardCard {

    @FXML private VBox rootVBox;
    @FXML private RadioButton focusButton;
    @FXML private RadioButton breakButton;
    @FXML private StackPane contentPane;
    @FXML private HBox paneControls;
    @FXML private ToggleGroup paneToggleGroup;

    private Map<Toggle, Node> tabViews;

    @FXML
    public void initialize() throws IOException {


        // https://stackoverflow.com/questions/71157873/fill-all-the-tabpane-width-with-tabs-in-javafx

        focusButton.getStyleClass().remove("radio-button");
        breakButton.getStyleClass().remove("radio-button");

        // combine this, dashboardcardfactory and main loader methods
        FXMLLoader focusLoader = new FXMLLoader(Main.class.getResource("fxml/FocusPomodoro.fxml"));
        Node focusContentRoot = focusLoader.load();

        FXMLLoader breakLoader = new FXMLLoader(Main.class.getResource("fxml/BreakPomodoro.fxml"));
        Node breakContentRoot = breakLoader.load();


        tabViews = Map.of(
                focusButton, focusContentRoot,
                breakButton, breakContentRoot
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
            Node node = tabViews.get(selectedToggle);
            if (node != null) {
                contentPane.getChildren().setAll(node);
            } else {
                contentPane.getChildren().clear();
            }
        } else {
            contentPane.getChildren().clear();
        }
    }

    public Node getView() {
        return rootVBox;
    }

    public void refresh() {
        // Refresh card
    }
}
