package com.example.devdash.model.typingtest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
    private int currentWordIndex;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean finished;

    private static final String[] NOUNS = {
            "cat","dog","government","city","problem","school","teacher","student","computer","program",
            "market","company","manager","project","idea","author","music","film","book","friend",
            "family","car","house","phone","internet","language","country","university","history","science",
            "animal","restaurant","game","movie","song","child","leader","artist","journal","community"
    };

    private static final String[] VERBS = {
            "run","consider","develop","analyze","create","manage","learn","teach","build","write",
            "read","play","watch","study","improve","solve","design","communicate","discover","explore",
            "decide","organize","explain","connect","perform","investigate","support","compare","increase","decrease"
    };

    private static final String[] ADJECTIVES = {
            "big","important","ancient","quick","interesting","modern","complex","difficult","simple","fast",
            "strong","young","old","creative","professional","friendly","expensive","popular","local","national",
            "international","academic","economic","cultural","famous","beautiful","amazing","successful","modern","rare"
    };

    private static final String[] CONJUNCTIONS = {
            "and","or","but","if","because","while","although","so","however","then",
            "when","where","after","before","unless","yet","as","though","once","until"
    };

    private static final String[] DEFAULT_WORD_POOL = {
            "ability","absence","academic","accepted","accident","activity","actually","addition","address",
            "advance","advice","agreement","almost","already","analysis","annual","answer","anybody","apparent",
            "approach","approval","argument","article","artist","assume","attention","average","balance","behavior"
    };

    private static final String[] PUNCTUATION_POOL = {
            ".", "!", "?", ",", ";", ":", "-", "..."
    };
    private static final double PUNCTUATION_PROB = 0.2;
    private final Random rand = new Random();

    /**
     * Constructs a new TypingTest.
     */
    public TypingTest() {
    }

    /**
     * Randomly generates words for the test from the default word pool.
     *
     * @param numberOfWords Number of words to generate
     */
    private void generateWords(int numberOfWords, boolean punctuation) {
        words = new Word[numberOfWords];
        int wordIndex = 0;

        while (wordIndex < numberOfWords) {
            int sentenceLength = 4 + rand.nextInt(7);

            for (int i = 0; i < sentenceLength && wordIndex < numberOfWords; i++) {
                String word;

                if (i == 0) {
                    word = getRandomWord("noun");
                } else if (i % 3 == 1) {
                    word = getRandomWord("verb");
                } else if (i % 3 == 2) {
                    word = getRandomWord("adjective");
                } else {
                    word = getRandomWord("conjunction");
                }

                if (punctuation && i == 0) word = Character.toUpperCase(word.charAt(0)) + word.substring(1);

                if (punctuation && i > 0 && i < sentenceLength - 1 && rand.nextDouble() < PUNCTUATION_PROB) {
                    word += PUNCTUATION_POOL[rand.nextInt(PUNCTUATION_POOL.length)];
                }

                words[wordIndex++] = new Word(word);
            }

            if (punctuation && wordIndex > 0) {
                Word lastWord = words[wordIndex - 1];
                String lastText = lastWord.getTarget(); // assuming getter
                if (!lastText.matches(".*[.!?]$")) {
                    lastText += PUNCTUATION_POOL[rand.nextInt(3)]; // only . ! ? for sentence end
                    words[wordIndex - 1] = new Word(lastText);
                }
            }
        }
    }


    /**
     * Example of getting a random word from category
     */
    private String getRandomWord(String category) {
        return switch (category) {
            case "noun" -> NOUNS[rand.nextInt(NOUNS.length)];
            case "verb" -> VERBS[rand.nextInt(VERBS.length)];
            case "adjective" -> ADJECTIVES[rand.nextInt(ADJECTIVES.length)];
            case "conjunction" -> CONJUNCTIONS[rand.nextInt(CONJUNCTIONS.length)];
            default -> DEFAULT_WORD_POOL[rand.nextInt(DEFAULT_WORD_POOL.length)];
        };
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
        if (startTime == null) startTime = LocalDateTime.now();

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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    // Statistic methods
    /**
     * Returns the total elapsed time in seconds since the start of the test.
     *
     * @return Elapsed time in seconds
     */
    public double getElapsedSeconds() {
        if (startTime == null) return 0;
        endTime = (finished && endTime != null) ? endTime : LocalDateTime.now();
        return Duration.between(startTime, endTime).toMillis() / 1000.0;
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
    public void reset(int numberOfWords, boolean punctuation) {
        generateWords(numberOfWords, punctuation);
        currentWordIndex = 0;
        startTime = null;
        endTime = null;
        finished = false;
    }
}
