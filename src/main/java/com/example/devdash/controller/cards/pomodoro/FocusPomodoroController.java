package com.example.devdash.controller.cards.pomodoro;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller for the Focus mode in the Pomodoro timer.
 * Sets a 25-minute timer and switches back to Focus mode when finished.
 *
 * Author: Alexander Sukhin
 * Version: 26/08/2025
 */
public class FocusPomodoroController extends AbstractPomodoroController {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private Integer currentSessionId = null;
    private LocalDateTime sessionStartTime = null;

    /**
     * This break session lasts 25 minutes.
     *
     * @return 25 minutes
     */
    @Override
    protected int getMinutes() {
        return prefs.getFocusTime(userId);
    }

    /**
     * Called when the focus timer finishes.
     * Transitions back to break mode on timer finish.
     */
    @Override
    protected void onTimerFinish() {
        endSession(true);
        if (cardController != null) cardController.switchToBreak();
    }

    /**
     * Adds a listener to the toggle button to start or pause the timer.
     * Updates the button text based on the current state.
     */
    @Override
    protected void setupToggleBehaviour() {
        pomodoroToggle.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                pomodoroToggle.setText("Pause");
                timeline.play();
                if (currentSessionId == null) createSession();
            } else {
                pomodoroToggle.setText("Start");
                timeline.pause();
            }
        });
    }

    /**
     * Creates a new focus session in the database.
     */
    private void createSession() {
        sessionStartTime = LocalDateTime.now();
        pomodoroModel.addSession(userId, sessionStartTime.format(FORMATTER), "0", 0, false);
        currentSessionId = pomodoroModel.getLastInsertedId();
    }

    /**
     * Ends the current focus session and updates the database.
     *
     * @param completed True if the session finished fully, false if interrupted
     */
    private void endSession(boolean completed) {
        if (currentSessionId == null) return;
        int duration = (int) java.time.Duration.between(sessionStartTime, LocalDateTime.now()).getSeconds();
        pomodoroModel.endSession(currentSessionId, LocalDateTime.now().format(FORMATTER), duration, completed);
        currentSessionId = null;
        sessionStartTime = null;
    }

    /**
     * Resets the focus timer and ends the current session if active.
     */
    @Override
    public void resetTime() {
        endSession(false);
        super.resetTime();
    }

}
