package com.example.devdash.controller.cards.typingtest;

import com.example.devdash.controller.cards.DashboardCard;
import com.example.devdash.helper.data.Session;
import com.example.devdash.helper.ui.FXMLUtils;
import com.example.devdash.model.typingtest.TypingTestModel;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for the Typing Test card in the dashboard.
 *
 * Author: Alexander Sukhin
 * Version: 28/08/2025
 */
public class TypingTestCardController implements DashboardCard {

    @FXML private VBox rootNode;
    @FXML private StackPane contentPane;

    @FXML private Button typingButton;     // fas-keyboard
    @FXML private Button statsButton;      // fas-info-circle
    @FXML private Button leaderboardButton;// fas-crown
    @FXML private Button settingsButton;   // fas-cog

    private final Map<Button, Node> buttonViews = new HashMap<>();
    private final Map<Button, TypingTestPaneController> buttonControllers = new HashMap<>();
    private Button currentButton;

    /**
     * Called automatically after the FXML file is loaded.
     * Initializes the controller by loading FXML sub panes,
     * setting up default view and binds actions for switching views.
     *
     * @throws IOException If any FXML loading fails
     * @throws SQLException If database errors occur during initialization
     */
    @FXML
    public void initialize() throws IOException, SQLException {
        loadPane(typingButton, "typingtest/TypingTest");
        loadPane(statsButton, "typingtest/Stats");
        loadPane(leaderboardButton, "typingtest/Leaderboard");
        loadPane(settingsButton, "typingtest/Settings");

        displayPane(typingButton);

        typingButton.setOnAction(e -> displayPane(typingButton));
        statsButton.setOnAction(e -> displayPane(statsButton));
        leaderboardButton.setOnAction(e -> displayPane(leaderboardButton));
        settingsButton.setOnAction(e -> displayPane(settingsButton));
    }

    /**
     * Loads an FXML view and its controller and maps them to the specified button.
     *
     * @param button The button associated with this view
     * @param fxmlName The FXML resource path to load
     * @throws IllegalStateException if the loaded controller does not implement TypingTestPaneController
     */
    private void loadPane(Button button, String fxmlName) {
        FXMLUtils loaded = FXMLUtils.loadFXML(fxmlName);
        Node root = loaded.getRoot();
        Object controller = loaded.getController();

        if (!(controller instanceof TypingTestPaneController paneController)) {
            throw new IllegalStateException(fxmlName + " must implement TypingTestPaneController");
        }

        buttonViews.put(button, root);
        buttonControllers.put(button, paneController);
    }
    /**
     * Displays the pane associated with the given button.
     *
     * @param button The button whose pane should be displayed
     */
    private void displayPane(Button button) {
        if (currentButton != null) {
            TypingTestPaneController prevController = buttonControllers.get(currentButton);
            if (prevController != null) prevController.resetPane();
        }

        contentPane.getChildren().setAll(buttonViews.get(button));
        buttonControllers.get(button).resetPane();
        currentButton = button;
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
