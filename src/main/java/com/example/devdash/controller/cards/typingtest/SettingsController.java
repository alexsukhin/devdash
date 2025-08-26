package com.example.devdash.controller.cards.typingtest;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;

import java.sql.SQLException;

public class SettingsController implements TypingTestPaneController {

    @FXML private RadioButton words10Radio;
    @FXML private RadioButton words25Radio;
    @FXML private RadioButton words50Radio;
    @FXML private RadioButton punctuationToggle;

    /**
     * Called automatically after the FXML file is loaded.
     * Sets up the test, cursor, and all input listeners.
     */
    @FXML
    public void initialize() throws SQLException {
        // Removes default radio button styling
        words10Radio.getStyleClass().remove("radio-button");
        words25Radio.getStyleClass().remove("radio-button");
        words50Radio.getStyleClass().remove("radio-button");
        punctuationToggle.getStyleClass().remove("radio-button");
    }

    @Override
    public void resetPane() {
    }
}
