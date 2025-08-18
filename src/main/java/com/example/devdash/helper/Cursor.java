package com.example.devdash.helper;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.Region;
import javafx.util.Duration;

/**
 * Represents a blinking caret (cursor) used in typing tests.
 *
 * Author: Alexander Sukhin
 * Version: 18/08/2025
 */
public class Cursor {

    private final Region cursor;

    /**
     * Constructs a new Cursor.
     * Initializes the Region and sets up a blinking animation.
     */
    public Cursor() {
        cursor = new Region();
        cursor.setPrefWidth(1);
        cursor.setMinHeight(0);
        cursor.setMaxHeight(20);
        cursor.setStyle("-fx-background-color: black;");

        Timeline blink = new Timeline(
                new KeyFrame(Duration.seconds(0.5), e -> cursor.setVisible(false)),
                new KeyFrame(Duration.seconds(1.0), e -> cursor.setVisible(true))
        );
        blink.setCycleCount(Timeline.INDEFINITE);
        blink.play();
    }

    /**
     * Returns the node representing this cursor.
     *
     * @return The Region node used as a caret
     */
    public Region getNode() {
        return cursor;
    }
}
