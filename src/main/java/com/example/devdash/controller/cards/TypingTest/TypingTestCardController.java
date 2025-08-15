package com.example.devdash.controller.cards.TypingTest;

import com.example.devdash.controller.cards.DashboardCard;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.time.LocalTime;
import java.util.Random;
import java.util.Scanner;

/**
 * Controller for the Typing Test card in the dashboard.
 *
 * Author: Alexander Sukhin
 * Version: 04/08/2025
 */
public class TypingTestCardController implements DashboardCard {

    @FXML private StackPane rootNode;
    @FXML private TextFlow textFlow;
    @FXML private Label focusLabel;
    @FXML private Label speed;
    @FXML private Label time;

    private Double start = null;
    private int currentIndex = 0;
    private boolean finished = false;

    private String[] words = {"envelope", "cantelope", "the", "hello", "microphone", "elephant", "biscuit", "hammer", "went", "cap"};
    private Random rand = new Random();
    private String targetText;

    /**
     * Handler for the Start button click event.
     */
    @FXML
    public void initialize() {
        generateText();
        updateDisplay();

        focusLabel.setText("Click here to start typing!");

        rootNode.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (finished) return;
            if (newVal) {
                focusLabel.setText("Start typing...");
            } else {
                focusLabel.setText("Click here to start typing!");
            }
        });

        rootNode.setOnMouseClicked(event -> {
            rootNode.requestFocus();
        });

        rootNode.setOnKeyTyped(event -> {
            String character = event.getCharacter();

            if (character.length() != 1) return;

            if (start == null) {
                start = (double) LocalTime.now().toNanoOfDay();
            }

            if (character.charAt(0) == targetText.charAt(currentIndex)) {
                currentIndex++;
                updateDisplay();

                if (currentIndex == targetText.length()) {
                    endTest();
                }
            }
        });

    }

    private void generateText() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            sb.append(words[rand.nextInt(words.length)]).append(" ");
        }
        targetText = sb.toString().trim();
    }

    private void updateDisplay() {
        textFlow.getChildren().clear();

        Text correctPart = new Text(targetText.substring(0, currentIndex));
        correctPart.setFill(Color.BLACK);
        correctPart.setStyle("-fx-font-size: 20px;");

        Text remainingPart = new Text(targetText.substring(currentIndex));
        remainingPart.setFill(Color.GRAY);
        remainingPart.setStyle("-fx-font-size: 20px;");

        textFlow.getChildren().addAll(correctPart, remainingPart);
    }


    private void endTest() {
        double end = LocalTime.now().toNanoOfDay();
        double elapsedTime = end - start;
        double seconds = elapsedTime / 1_000_000_000.0;

        double wordsTyped = targetText.length() / 5.0;
        double minutes = seconds / 60.0;
        double wpm = wordsTyped / minutes;

        speed.setText(String.format("WPM: %.2f", wpm));
        time.setText(String.format("Time: %.2f seconds", seconds));

        finished = true;
        focusLabel.setText("Finished! Press reset to try again");
    }

    public void resetText() {
        currentIndex = 0;
        start = null;
        finished = false;
        generateText();
        updateDisplay();
        speed.setText("WPM:");
        time.setText("Time:");

        focusLabel.setText("Start typing...");
        rootNode.requestFocus();
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
