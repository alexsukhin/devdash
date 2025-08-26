package com.example.devdash.controller.cards.typingtest;

import com.example.devdash.controller.cards.DashboardCard;
import com.example.devdash.helper.ui.Cursor;
import com.example.devdash.model.typingtest.TypingTest;
import com.example.devdash.model.typingtest.Word;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.sql.SQLException;

/**
 * Controller for the Typing Test card in the dashboard.
 *
 * Author: Alexander Sukhin
 * Version: 18/08/2025
 */
public class TypingTestController implements TypingTestPaneController {

    @FXML private VBox rootNode;
    @FXML private TextFlow textFlow;
    @FXML private Label focusLabel;
    @FXML private Label speed;
    @FXML private Label time;
    @FXML private Label accuracy;


    private TypingTest test;
    private Cursor cursor;

    /**
     * Called automatically after the FXML file is loaded.
     * Sets up the test, cursor, and all input listeners.
     */
    @FXML
    public void initialize() throws SQLException {
        test = new TypingTest(10);
        cursor = new Cursor();

        setupFocusListener();
        setupMouseClickListener();
        setupKeyListener();

        updateDisplay();
        focusLabel.setText("Click here to start typing!");
    }

    /**
     * Updates the focus label based on whether the card is focused or not.
     * If the test is finished, focus changes do not affect the label.
     */
    private void setupFocusListener() {
        rootNode.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (test.isFinished()) return;
            focusLabel.setText(newVal ? "Start typing..." : "Click here to start typing!");
        });
    }

    /**
     * Ensures clicking anywhere on the root node will request focus,
     * so the user can start typing.
     */
    private void setupMouseClickListener() {
        rootNode.setOnMouseClicked(event -> rootNode.requestFocus());
    }

    /**
     * Handles key typed events.
     */
    private void setupKeyListener() {
        rootNode.setOnKeyTyped(event -> {
            String character = event.getCharacter();

            // Returns if multi-character inputs or finished test
            if (character.length() != 1 || test.isFinished()) return;

            // Update the model
            test.typeChar(character.charAt(0));

            // Refresh the visual display
            try {
                updateDisplay();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            if (test.isFinished()) endTest();
        });
    }

    /**
     * Updates the TextFlow display with all words and spaces.
     * Highlights typed letters, errors, and shows the caret at the current word.
     */
    private void updateDisplay() throws SQLException {
        textFlow.getChildren().clear();
        Word[] words = test.getWords();
        int currentIndex = test.getCurrentWordIndex();
        Region cursorNode = cursor.getNode();

        for (int i = 0; i < words.length; i++) {
            Word word = words[i];
            boolean showCaret = i == currentIndex;
            for (Node n : word.getStyledText(showCaret, cursorNode)) textFlow.getChildren().add(n);

            textFlow.getChildren().add(createSpace());
        }
    }

    /**
     * Creates a Text node representing a space character.
     *
     * @return A Text node containing a single space
     */
    private Text createSpace() {
        Text space = new Text(" ");
        space.setStyle("-fx-font-size: 20px;");
        return space;
    }


    /**
     * Handles end-of-test logic:
     * Updates speed, time, and accuracy labels,
     * and changes focusLabel to indicate the test has finished.
     */
    private void endTest() {
        speed.setText(String.format("WPM: %.1f", test.getWPM()));
        time.setText(String.format("Time: %.2f seconds", test.getElapsedSeconds()));
        accuracy.setText(String.format("Accuracy: %.0f%%", test.getAccuracyPercent()));
        focusLabel.setText("Finished! Press reset to try again");
    }

    /**
     * Resets the typing test to the initial state:
     * clears the typed words, resets metrics, and updates the display.
     */
    @FXML
    public void resetText() throws SQLException {
        test.reset();
        updateDisplay();
        speed.setText("WPM:");
        time.setText("Time:");
        accuracy.setText("Accuracy:");
        focusLabel.setText("Start typing...");
        rootNode.requestFocus();
    }

    @Override
    public void resetPane() {
        try {
            test.reset();
            updateDisplay();
            speed.setText("WPM:");
            time.setText("Time:");
            accuracy.setText("Accuracy:");
            focusLabel.setText("Start typing...");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
