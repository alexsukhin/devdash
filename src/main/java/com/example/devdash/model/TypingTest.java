package com.example.devdash.model;

import java.time.LocalTime;
import java.util.Random;


/**
 * Represents a typing test session with a fixed number of words.
 * Tracks typing progress, calculates statistics, and manages test state.
 *
 * Author: Alexander Sukhin
 * Version: 18/08/2025
 */
public class TypingTest {

    private Word[] words;
    private int currentWordIndex = 0;
    private Double startTime = null;
    private Double endTime = null;
    private boolean finished = false;

    private static final String[] DEFAULT_WORD_POOL = {
            "envelope", "cantelope", "the", "hello",
            "microphone", "elephant", "biscuit", "hammer",
            "went", "cap"
    };
    private final Random rand = new Random();

    /**
     * Constructs a new TypingTest with a given number of words.
     *
     * @param numberOfWords The total number of words in the test
     */
    public TypingTest(int numberOfWords) {
        generateWords(numberOfWords);
    }

    /**
     * Randomly generates words for the test from the default word pool.
     *
     * @param numberOfWords Number of words to generate
     */
    private void generateWords(int numberOfWords) {
        words = new Word[numberOfWords];
        for (int i = 0; i < numberOfWords; i++) {
            words[i] = new Word(DEFAULT_WORD_POOL[rand.nextInt(DEFAULT_WORD_POOL.length)]);
        }
    }

    /**
     * Processes a typed character.
     * Space moves to the next word,
     * Backspace removes the last typed character,
     * Other characters are appended to the current word.
     *
     * @param c The character typed by the user
     */
    public void typeChar(char c) {
        if (finished) return;
        if (startTime == null) startTime = (double) LocalTime.now().toNanoOfDay();

        Word currentWord = getCurrentWord();

        switch (c) {
            case ' ' -> {
                currentWordIndex++;
                if (currentWordIndex >= words.length) finished = true;
            }
            case '\b' -> currentWord.removeChar();
            default -> currentWord.appendChar(c);
        }
    }

    /**
     * Returns the current word being typed.
     *
     * @return The current Word object, or null if test is finished
     */
    public Word getCurrentWord() {
        if (currentWordIndex < words.length) return words[currentWordIndex];
        return null;
    }

    /**
     * Returns all words in this test.
     *
     * @return Array of Word objects
     */
    public Word[] getWords() {
        return words;
    }

    /**
     * Returns the index of the current letter being typed.
     *
     * @return Index of current word
     */
    public int getCurrentWordIndex() {
        return currentWordIndex;
    }

    /**
     * Checks if the test is finished.
     *
     * @return True if finished, false otherwise
     */
    public boolean isFinished() {
        return finished;
    }

    // Statistic methods


    /**
     * Returns the total elapsed time in seconds since the start of the test.
     *
     * @return Elapsed time in seconds
     */
    public double getElapsedSeconds() {
        if (startTime == null) return 0;
        endTime = (double) LocalTime.now().toNanoOfDay();
        return (endTime - startTime) / 1_000_000_000.0;
    }

    /**
     * Counts the total number of correctly typed words.
     *
     * @return Number of correct words
     */
    public int getCorrectWords() {
        int correctCount = 0;
        for (Word word : words) {
            if (word.isCorrect()) correctCount++;
        }
        return correctCount;
    }

    /**
     * Returns the accuracy as a percentage (0–100) based on correct words.
     *
     * @return Accuracy percentage
     */
    public double getAccuracyPercent() {
        int totalWords = words.length;
        return ((double) getCorrectWords() / totalWords) * 100.0;
    }

    /**
     * Calculates typing speed in words per minute.
     * Uses the standard formula: (correct chars / 5) / minutes elapsed.
     *
     * @return Words per minute
     */
    public double getWPM() {
        double minutes = getElapsedSeconds() / 60.0;
        if (minutes == 0) return 0;

        int correctChars = 0;
        for (Word word : words) {
            if (word.isCorrect()) correctChars += word.getLength();
        }

        return (correctChars / 5.0) / minutes;
    }

    /**
     * Resets the typing test for a new attempt.
     * Re-generates words, resets indices, timers, and finished flag.
     */
    public void reset() {
        currentWordIndex = 0;
        startTime = null;
        endTime = null;
        finished = false;
        generateWords(words.length);
    }
}
