package com.example.devdash.controller.cards.typingtest;

import com.example.devdash.model.typingtest.TypingSession;
import com.example.devdash.model.typingtest.TypingTestModel;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Controller for the leaderboard pane.
 *
 * Author: Alexander Sukhin
 * Version: 28/08/2025
 */
public class LeaderboardController implements TypingTestPaneController {

    @FXML private VBox sessionsContainer;
    @FXML private ToggleGroup punctuationToggle;
    @FXML private ToggleGroup wordToggle;
    @FXML private ToggleButton words10Toggle;
    @FXML private ToggleButton words25Toggle;
    @FXML private ToggleButton words50Toggle;
    @FXML private ToggleButton punctuationOff;
    @FXML private ToggleButton punctuationOn;

    private TypingTestModel typingModel;
    private boolean punctuation;
    private int testLength;

    /**
     * Called automatically after the FXML file is loaded.
     * Initializes the leaderboard UI, loads sessions, and sets up toggle listeners.
     */
    @FXML
    public void initialize() {
        typingModel = new TypingTestModel();

        initializeDefaults();
        setupToggleListeners();
        loadSessions();
    }

    /**
     * Sets default toggle selections and initial state.
     */
    private void initializeDefaults() {
        punctuationToggle.selectToggle(punctuationOff);
        punctuation = false;

        wordToggle.selectToggle(words10Toggle);
        testLength = 10;
    }

    /**
     * Adds listeners to the toggles for test length and punctuation,
     * reloading sessions on change.
     */
    private void setupToggleListeners() {
        wordToggle.selectedToggleProperty().addListener((obs, old, selected) -> {
            if (selected == null) {
                wordToggle.selectToggle(old);
                return;
            }

            if (selected == words10Toggle) {
                testLength = 10;
            } else if (selected == words25Toggle) {
                testLength = 25;
            } else if (selected == words50Toggle) {
                testLength = 50;
            }

            loadSessions();
        });

        punctuationToggle.selectedToggleProperty().addListener((obs, old, selected) -> {
            if (selected == null) {
                punctuationToggle.selectToggle(old);
                return;
            }

            punctuation = selected == punctuationOn;
            loadSessions();
        });
    }

    /**
     * Loads the quickest typing sessions based on the current test length and punctuation filter,
     * then populates the UI list.
     */
    private void loadSessions() {
        if (typingModel == null) return;

        List<TypingSession> sessions = typingModel.getQuickestSessions(testLength, punctuation);
        sessionsContainer.getChildren().clear();

        for (int i = 0; i < sessions.size(); i++) {
            TypingSession session = sessions.get(i);
            HBox sessionBox = createSessionNode(i + 1, session); // Pass the rank/index
            if ((i + 1) % 2 == 1) sessionBox.getStyleClass().add("session-box");
            sessionsContainer.getChildren().add(sessionBox);
        }
    }

    /**
     * Creates an HBox UI node representing one leaderboard session entry.
     *
     * @param rank rank number in the leaderboard (starting from 1)
     * @param session the TypingSession object
     * @return configured HBox containing session data
     */
    private HBox createSessionNode(int rank, TypingSession session) {
        HBox hbox = new HBox();
        hbox.setSpacing(5);
        hbox.setMaxWidth(410);
        hbox.setAlignment(Pos.CENTER_LEFT);

        Label rankLabel = new Label(String.valueOf(rank));
        rankLabel.setPrefWidth(30);
        rankLabel.setAlignment(Pos.CENTER);

        Label nameLabel = new Label(session.getUsername());
        nameLabel.setPrefWidth(130);

        Label wpmLabel = new Label(String.format("%.1f", session.getWpm()));
        wpmLabel.setPrefWidth(50);
        wpmLabel.setAlignment(Pos.CENTER);

        Label accuracyLabel = new Label(String.format("%.1f%%", session.getAccuracy()));
        accuracyLabel.setPrefWidth(100);
        accuracyLabel.setAlignment(Pos.CENTER);

        VBox dateBox = new VBox();
        dateBox.setPrefWidth(100);
        dateBox.setAlignment(Pos.CENTER);
        dateBox.getStyleClass().add("font-time");

        String[] dateParts = session.getStartTime().split(" ");
        Label dateLabel = new Label(dateParts[0]);
        Label dateTimeLabel = new Label(dateParts.length > 1 ? dateParts[1] : "");

        dateBox.getChildren().addAll(dateLabel, dateTimeLabel);

        hbox.getChildren().addAll(rankLabel, nameLabel, wpmLabel, accuracyLabel, dateBox);
        hbox.setMinHeight(40);

        return hbox;
    }

    /**
     * Resets the leaderboard page to the initial state.
     */
    @Override
    public void resetPane() {
        loadSessions();
    }
}


