package com.example.devdash.controller.cards.typingtest;

import com.example.devdash.helper.data.Session;
import com.example.devdash.model.auth.PreferencesModel;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

import java.sql.SQLException;


/**
 * Controller for the typing test settings pane.
 *
 * Author: Alexander Sukhin
 * Version: 28/08/2025
 */
public class SettingsController implements TypingTestPaneController {

    @FXML public ToggleGroup wordToggle;
    @FXML public ToggleGroup punctuationToggle;
    @FXML private ToggleButton words10Toggle;
    @FXML private ToggleButton words25Toggle;
    @FXML private ToggleButton words50Toggle;
    @FXML private ToggleButton punctuationOff;
    @FXML private ToggleButton punctuationOn;

    private final PreferencesModel prefs = PreferencesModel.getInstance();
    protected final int userId = Session.getInstance().getUserId();

    /**
     * Called automatically after the FXML file is loaded.
     * Initializes UI components with saved preferences and sets up listeners.
     *
     * @throws SQLException if database access error occurs while reading preferences
     */
    @FXML
    public void initialize() throws SQLException {
        loadPreferences();
        setupWordToggleListener();
        setupPunctuationToggleListener();
    }

    /**
     * Loads saved preferences from the database and sets the initial toggle selections.
     *
     */
    private void loadPreferences() {
        int testLength = prefs.getTestLength(userId);
        boolean punctuation = prefs.getPunctuationBool(userId);

        switch (testLength) {
            case 25 -> wordToggle.selectToggle(words25Toggle);
            case 50 -> wordToggle.selectToggle(words50Toggle);
            default -> wordToggle.selectToggle(words10Toggle);
        }
        punctuationToggle.selectToggle(punctuation ? punctuationOn : punctuationOff);
    }

    /**
     * Sets up the listener on the word count toggle group to save changes when toggled.
     */
    private void setupWordToggleListener() {
        wordToggle.selectedToggleProperty().addListener((obs, old, selected) -> {
            if (selected == null) {
                // Prevent deselecting all toggles, revert to old toggle
                wordToggle.selectToggle(old);
                return;
            }

            int length = 10;
            if (selected == words25Toggle) length = 25;
            else if (selected == words50Toggle) length = 50;

            prefs.updateTestLength(userId, length);
            System.out.println("Saved test length = " + length);
        });
    }

    /**
     * Sets up the listener on the punctuation toggle group to save changes when toggled.
     */
    private void setupPunctuationToggleListener() {
        punctuationToggle.selectedToggleProperty().addListener((obs, old, selected) -> {
            if (selected == null) {
                // Prevent deselecting all toggles, revert to old toggle
                punctuationToggle.selectToggle(old);
                return;
            }

            boolean enabled = (selected == punctuationOn);
            prefs.updatePunctuationBool(userId, enabled ? 1 : 0);
            System.out.println("Saved punctuation = " + enabled);
        });
    }

    /**
     * Resets the settings page to the initial state.
     */
    @Override
    public void resetPane() {
        // No reset behavior required currently
    }
}
