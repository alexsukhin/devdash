package com.example.devdash.controller.cards.Pomodoro;

import com.example.devdash.helper.Session;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Focus Pomodoro Controller.
 * Handles creating and ending focus sessions.
 */
public class FocusPomodoroController extends AbstractPomodoroController {

    @FXML private ToggleButton pomodoroToggle;

    private Integer currentSessionId = null;
    private LocalDateTime sessionStartTime = null;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * This break session lasts 25 minutes.
     *
     * @return 5 minutes
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
        if (switchHandler != null) {
            switchHandler.switchToBreak();
        }
    }

    /**
     * Called automatically after the FXML file is loaded.
     * Creates a DB session on start.
     */
    @FXML
    @Override
    public void initialize() {
        super.initialize();
        setupFocusToggleBehavior();
    }

    /**
     * Sets up the start/pause toggle button specifically for Focus mode.
     */
    private void setupFocusToggleBehavior() {
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
        pomodoroModel.addSession(Session.getInstance().getUserId(),
                sessionStartTime.format(FORMATTER),
                "0", 0, false);
        currentSessionId = pomodoroModel.getLastInsertedId();
    }

    /**
     * Ends the current focus session and updates the database.
     *
     * @param completed True if the session finished fully, false if interrupted
     */
    protected void endSession(boolean completed) {
        if (currentSessionId == null) return;
        int duration = (int) Duration.between(sessionStartTime, LocalDateTime.now()).getSeconds();
        pomodoroModel.endSession(currentSessionId,
                LocalDateTime.now().format(FORMATTER),
                duration,
                completed);
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
