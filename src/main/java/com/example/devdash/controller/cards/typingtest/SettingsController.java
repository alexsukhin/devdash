package com.example.devdash.controller.cards.typingtest;

import com.example.devdash.helper.data.Session;
import com.example.devdash.model.auth.PreferencesModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

import java.sql.SQLException;

public class SettingsController implements TypingTestPaneController {

    @FXML public ToggleGroup wordToggle;
    @FXML public ToggleGroup punctuationToggle;
    @FXML private ToggleButton words10Toggle;
    @FXML private ToggleButton words25Toggle;
    @FXML private ToggleButton words50Toggle;
    @FXML private ToggleButton punctuationOff;
    @FXML private ToggleButton punctuationOn;
    @FXML private ToggleGroup togglePane;

    private final PreferencesModel prefs = PreferencesModel.getInstance();
    protected final int userId = Session.getInstance().getUserId();

    /**
     * Called automatically after the FXML file is loaded.
     * Sets up the test, cursor, and all input listeners.
     */
    @FXML
    public void initialize() throws SQLException {
        int testLength = prefs.getTestLength(userId);
        boolean punctuation = prefs.getPunctuationBool(userId);

        switch (testLength) {
            case 25 -> wordToggle.selectToggle(words25Toggle);
            case 50 -> wordToggle.selectToggle(words50Toggle);
            default -> wordToggle.selectToggle(words10Toggle);
        }
        punctuationToggle.selectToggle(punctuation ? punctuationOn : punctuationOff);

        wordToggle.selectedToggleProperty().addListener((obs, old, selected) -> {
            if (selected == null) {
                // Prevent unselecting
                wordToggle.selectToggle(old);
                return;
            }

            wordToggle.selectToggle(selected);
            int length = 10;
            if (selected == words25Toggle) length = 25;
            else if (selected == words50Toggle) length = 50;
            prefs.updateTestLength(userId, length);
            System.out.println("Saved test length = " + length);
        });

        punctuationToggle.selectedToggleProperty().addListener((obs, old, selected) -> {
            if (selected == null) {
                // Prevent unselecting
                punctuationToggle.selectToggle(old);
                return;
            }
            boolean enabled = (selected == punctuationOn);
            prefs.updatePunctuationBool(userId, enabled ? 1 : 0);
            System.out.println("Saved punctuation = " + enabled);
        });

    }

    @Override
    public void resetPane() {
    }
}
