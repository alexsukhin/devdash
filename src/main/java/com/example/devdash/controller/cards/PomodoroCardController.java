package com.example.devdash.controller.cards;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.w3c.dom.Text;
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


        focusButton.getStyleClass().remove("radio-button");
        breakButton.getStyleClass().remove("radio-button");

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
                contentPane.getChildren().clear(); // fallback clear if no pane mapped
            }
        } else {
            contentPane.getChildren().clear(); // fallback clear if no toggle selected
        }
    }

    public Node getView() {
        return rootVBox;
    }

    public void refresh() {
        // Refresh card
    }
}
