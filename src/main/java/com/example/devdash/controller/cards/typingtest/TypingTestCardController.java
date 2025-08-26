package com.example.devdash.controller.cards.typingtest;

import com.example.devdash.controller.cards.DashboardCard;
import com.example.devdash.helper.ui.Cursor;
import com.example.devdash.helper.ui.FXMLUtils;
import com.example.devdash.model.typingtest.TypingTest;
import com.example.devdash.model.typingtest.Word;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for the Typing Test card in the dashboard.
 *
 * Author: Alexander Sukhin
 * Version: 18/08/2025
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


    @FXML
    public void initialize() throws IOException {
        System.out.println("test");
        // Load all sub-panes
        loadPane(typingButton, "typingtest/TypingTest");
        loadPane(statsButton, "typingtest/Stats");
        loadPane(leaderboardButton, "typingtest/Leaderboard");
        loadPane(settingsButton, "typingtest/Settings");

        // Default view
        displayPane(typingButton);

        // Setup button actions
        typingButton.setOnAction(e -> displayPane(typingButton));
        statsButton.setOnAction(e -> displayPane(statsButton));
        leaderboardButton.setOnAction(e -> displayPane(leaderboardButton));
        settingsButton.setOnAction(e -> displayPane(settingsButton));
    }


    private void loadPane(Button button, String fxmlName) throws IOException {
        FXMLUtils loaded = FXMLUtils.loadFXML(fxmlName);
        Node root = loaded.getRoot();
        Object controller = loaded.getController();

        if (!(controller instanceof TypingTestPaneController paneController)) {
            throw new IllegalStateException(fxmlName + " must implement TypingTestPaneController");
        }

        buttonViews.put(button, root);
        buttonControllers.put(button, paneController);
    }

    private void displayPane(Button button) {
        // Reset previous pane if any
        if (currentButton != null) {
            TypingTestPaneController prevController = buttonControllers.get(currentButton);
            if (prevController != null) prevController.resetPane();
        }

        // Show new pane
        contentPane.getChildren().setAll(buttonViews.get(button));
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
