package com.example.devdash.controller.cards.Pomodoro;

import java.sql.SQLException;

/**
 * Controller for the Break mode in the Pomodoro timer.
 * Sets a 5-minute timer and switches back to Focus mode when finished.
 *
 * Author: Alexander Sukhin
 * Version: 04/08/2025
 */
public class BreakPomodoroController extends AbstractPomodoroController {


    /**
     * This break session lasts 5 minutes.
     *
     * @return 5 minutes
     */
    @Override
    protected int getMinutes() {
        return prefs.getBreakTime(userId);
    }

    /**
     * Called when the break timer finishes.
     * Transitions back to focus mode on timer finish.
     */
    @Override
    protected void onTimerFinish() {
        if (switchHandler != null) {
            switchHandler.switchToFocus();
        }
    }
}

