package com.example.devdash.controller.cards.Pomodoro;

/**
 * Interface representing a controller for a Pomodoro timer pane.
 *
 * Author: Alexander Sukhin
 * Version: 04/08/2025
 */
public interface PomodoroPaneController {

    /**
     * Resets the Pomodoro timer pane to its initial state.
     */
    void reset();

    /**
     * Sets the handler responsible for switching between Pomodoro modes.
     */
    void setSwitchHandler(PomodoroCardController pomodoroCardController);
}