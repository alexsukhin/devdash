package com.example.devdash.controller.cards.pomodoro;

/**
 * Interface representing a controller for a Pomodoro timer pane.
 *
 * Author: Alexander Sukhin
 * Version: 04/08/2025
 */
public interface PomodoroPaneController {

    /**
     * Resets the timer to its initial state.
     * Called by the UI reset button.
     */
    void resetTime();

    /**
     * Updates the timer label and the "Today's Focus" label.
     * Displays time in minutes and seconds.
     */
    void updateTimerLabel();

    /**
     * Sets the controller responsible for switching between Focus and Break panes.
     */
    void setCardController(PomodoroCardController pomodoroCardController);
}