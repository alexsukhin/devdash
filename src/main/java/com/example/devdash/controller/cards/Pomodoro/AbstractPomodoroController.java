package com.example.devdash.controller.cards.Pomodoro;

import com.example.devdash.model.PomodoroSession;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import javafx.scene.control.ToggleButton;
import javafx.util.Duration;

/**
 * Abstract base controller for Pomodoro timer panes.
 * Handles timer countdown, start/pause toggle, and reset logic.
 *
 * Author: Alexander Sukhin
 * Version: 04/08/2025
 */
public abstract class AbstractPomodoroController implements PomodoroPaneController {

    @FXML private Label timer;
    @FXML private ToggleButton pomodoroToggle;

    protected Timeline timeline;
    protected PomodoroSession pomodoroSession;
    protected PomodoroSwitchHandler switchHandler;

    /**
     * Subclasses must provide the duration of the timer in minutes.
     *
     * @return The length of this Pomodoro session in minutes
     */
    protected abstract int getMinutes();

    /**
     * Called when the timer reaches zero.
     * Subclasses define what happens next.
     */
    protected abstract void onTimerFinish();

    /**
     * Called automatically after the FXML file is loaded.
     * Initializes the PomodoroSession, timeline, toggle button, and timer label.
     */
    @FXML
    public void initialize() {
        pomodoroSession = new PomodoroSession(getMinutes());
        setupTimeline();
        setupToggleBehavior();
        updateTimerLabel();
    }

    /**
     * Sets up the Timeline to tick every second and call tick() method.
     */
    private void setupTimeline() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> tick()));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    /**
     * Adds a listener to the toggle button to start or pause the timer.
     */
    private void setupToggleBehavior() {
        pomodoroToggle.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                pomodoroToggle.setText("Pause");
                timeline.play();
            } else {
                pomodoroToggle.setText("Start");
                timeline.pause();
            }
        });
    }

    /**
     * Called every second by the timeline.
     * Decrements the timer, updates label, and handles timer completion.
     */
    private void tick() {
        pomodoroSession.oneSecondPassed();
        updateTimerLabel();

        if (pomodoroSession.isFinished()) {
            timeline.stop();
            pomodoroToggle.setSelected(false);
            pomodoroToggle.setText("Start");
            onTimerFinish();
        }
    }

    /**
     * Resets the timer state.
     */
    @FXML
    public void resetTime() {
        this.reset();
    }

    /**
     * Resets the timer and UI to the initial state.
     */
    @Override
    public void reset() {
        if (timeline != null) {
            timeline.stop();
        }
        pomodoroSession.reset();
        updateTimerLabel();
        pomodoroToggle.setSelected(false);
        pomodoroToggle.setText("Start");
    }

    /**
     * Updates the timer label with the current time string.
     */
    private void updateTimerLabel() {
        timer.setText(pomodoroSession.getCurrentTime());
    }

    /**
     * Sets the handler responsible for switching between Pomodoro modes.
     *
     * @param handler The PomodoroSwitchHandler implementation to handle mode switching
     */
    @Override
    public void setSwitchHandler(PomodoroCardController handler) {
        this.switchHandler = handler;
    }

}