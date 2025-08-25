package com.example.devdash.controller.cards.pomodoro;

/**
 * Interface to handle switching between Pomodoro timer modes.
 *
 * Author: Alexander Sukhin
 * Version: 04/08/2025
 */
public interface PomodoroSwitchHandler {

    /**
     * Switches the timer to Break mode.
     */
    void switchToBreak();

    /**
     * Switches the timer to Focus mode.
     */
    void switchToFocus();
}