package com.example.devdash.model;


import com.example.devdash.helper.Session;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single word in the typing test.
 * Tracks typed characters, caret position, and provides styled nodes for display.
 *
 * Author: Alexander Sukhin
 * Version: 18/08/2025
 */
public class Word {

    private final String target;
    private String typed = "";
    private int caretIndex = 0;

    /**
     * Constructs a Word object with the specified target.
     *
     * @param target The word the user should type
     */
    public Word(String target) {
        this.target = target;
    }

    /**
     * Appends a character to the typed word and moves the caret forward.
     *
     * @param c Character typed by the user
     */
    public void appendChar(char c) {
        typed += c;
        caretIndex++;
    }

    /**
     * Removes the last typed character and moves the caret backward.
     */
    public void removeChar() {
        if (caretIndex > 0) {
            typed = typed.substring(0, typed.length() - 1);
            caretIndex--;
        }
    }

    /**
     * Returns the length of the target word.
     *
     * @return Length of the target word
     */
    public int getLength() {
        return target.length();
    }

    /**
     * Checks if the typed word matches the target word exactly.
     *
     * @return True if the typed word is correct, false otherwise
     */
    public boolean isCorrect() {
        return typed.equals(target);
    }

    /**
     * Generates an array of styled nodes representing the word for display.
     * Optionally includes a cursor at the caret position.
     *
     * @param showCaret  Whether to show the caret
     * @param cursorNode The Node to display as the caret
     * @return Array of Node objects representing the styled word
     */
    public Node[] getStyledText(boolean showCaret, Region cursorNode) {
        List<Node> nodes = new ArrayList<>();
        int len = Math.max(typed.length(), target.length());

        for (int i = 0; i < len; i++) {
            if (showCaret && i == caretIndex) nodes.add(cursorNode);
            nodes.add(createLetterNode(i));
        }

        if (showCaret && caretIndex == len) nodes.add(cursorNode);

        return nodes.toArray(new Node[0]);
    }

    /**
     * Creates a single Text node for a letter at a given index, applying appropriate styling.
     * Letters are colored black if correct, red if incorrect, gray if not yet typed.
     *
     * @param index The index of the letter to create
     * @return A Text node representing the styled letter
     */
    public Text createLetterNode(int index) {
        Text t;
        if (index < target.length()) {
            t = new Text(String.valueOf(target.charAt(index)));
            if (index < typed.length()) {
                Color correctColor = Session.getInstance().isDark() ? Color.WHITE : Color.BLACK;
                t.setFill(typed.charAt(index) == target.charAt(index) ? correctColor : Color.RED);
            } else {
                t.setFill(Color.GRAY);
            }
        } else {
            t = new Text(String.valueOf(typed.charAt(index)));
            t.setFill(Color.RED);
            t.setOpacity(0.4);
        }

        t.setStyle("-fx-font-size: 20px;");

        return t;
    }

}
