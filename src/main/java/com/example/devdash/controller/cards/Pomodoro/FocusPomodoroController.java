package com.example.devdash.controller.cards.Pomodoro;

/**
 * Controller for the Focus mode in the Pomodoro timer.
 * Sets a 25-minute timer and switches back to Break mode when finished.
 *
 * Author: Alexander Sukhin
 * Version: 04/08/2025
 */
public class FocusPomodoroController extends AbstractPomodoroController {

    /**
     * This focus session lasts 25 minutes.
     *
     * @return 25 minutes
     */
    @Override
    protected int getMinutes() {
        return 25;
    }

    /**
     * Called when the focus timer finishes.
     * Transitions back to break mode on timer finish.
     */
    @Override
    protected void onTimerFinish() {
        if (switchHandler != null) {
            switchHandler.switchToBreak();
        }
    }
}