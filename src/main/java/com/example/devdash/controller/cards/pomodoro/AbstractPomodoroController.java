package com.example.devdash.controller.cards.pomodoro;

import com.example.devdash.helper.data.Session;
import com.example.devdash.model.pomodoro.PomodoroModel;
import com.example.devdash.model.pomodoro.PomodoroTimer;
import com.example.devdash.model.auth.PreferencesModel;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.Duration;

/**
 * Base controller for Pomodoro timer panes (Focus & Break).
 * Handles timer countdown, start/pause toggle, reset, and label updates.
 *
 * Author: Alexander Sukhin
 * Version: 26/08/2025
 */
public abstract class AbstractPomodoroController implements PomodoroPaneController {

    @FXML protected Label timer;
    @FXML protected ToggleButton pomodoroToggle;
    @FXML protected Label timeFocused;

    protected Timeline timeline;
    protected PomodoroTimer pomodoroTimer;
    protected final PomodoroModel pomodoroModel = new PomodoroModel();
    protected final PreferencesModel prefs = PreferencesModel.getInstance();
    protected final int userId = Session.getInstance().getUserId();
    protected PomodoroCardController cardController;

    /**
     * Subclasses must define the duration of the timer in minutes.
     *
     * @return duration of the Pomodoro session in minutes
     */
    protected abstract int getMinutes();

    /**
     * Called automatically when the timer reaches zero.
     */
    protected abstract void onTimerFinish();

    /**
     * Adds a listener to the toggle button to start or pause the timer.
     * Updates the button text based on the current state.
     */
    protected abstract void setupToggleBehaviour();

    /**
     * Called automatically after the FXML file is loaded.
     * Sets up the timer, timeline, toggle behaviour and timer label.
     */
    @FXML
    public void initialize() {
        pomodoroTimer = new PomodoroTimer(getMinutes());
        setupTimeline();
        setupToggleBehaviour();
        updateTimerLabel();
    }

    /**
     * Sets up the Timeline to call Tick every second.
     * Runs indefinitely until stopped.
     */
    private void setupTimeline() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> tick()));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    /**
     * Called every second by the timeline.
     * Decrements the timer, updates the timer label.
     */
    private void tick() {
        pomodoroTimer.oneSecondPassed();
        updateTimerLabel();

        if (pomodoroTimer.isFinished()) {
            timeline.stop();
            pomodoroToggle.setSelected(false);
            pomodoroToggle.setText("Start");
            onTimerFinish();
        }
    }

    /**
     * Updates the timer label and the "Today's Focus" label.
     * Displays time in minutes and seconds.
     */
    public void updateTimerLabel() {
        timer.setText(pomodoroTimer.getCurrentTime());
        if (timeFocused != null) {
            int totalSecondsToday = pomodoroModel.getTodayTotalDuration(userId);
            timeFocused.setText("Today's Focus: " + (totalSecondsToday / 60) + " min " + (totalSecondsToday % 60) + " sec");
        }
    }

    /**
     * Opens a settings dialog allowing the user to update Focus and Break times.
     * Updates the database when the user confirms.
     */
    public void changeTime() {
        int currentFocus = prefs.getFocusTime(userId);
        int currentBreak = prefs.getBreakTime(userId);

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Pomodoro Settings");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        Label focusLabel = new Label("Focus Time (minutes):");
        Spinner<Integer> focusSpinner = new Spinner<>();
        focusSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 180, currentFocus));

        Label breakLabel = new Label("Break Time (minutes):");
        Spinner<Integer> breakSpinner = new Spinner<>();
        breakSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 60, currentBreak));

        grid.add(focusLabel, 0, 0);
        grid.add(focusSpinner, 1, 0);
        grid.add(breakLabel, 0, 1);
        grid.add(breakSpinner, 1, 1);

        GridPane.setHgrow(focusSpinner, Priority.ALWAYS);
        GridPane.setHgrow(breakSpinner, Priority.ALWAYS);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                prefs.updateFocusTime(userId, focusSpinner.getValue());
                prefs.updateBreakTime(userId, breakSpinner.getValue());
                resetTime();
            }
            return null;
        });

        dialog.showAndWait();
    }

    /**
     * Resets the timer to its initial state.
     * Called by the UI reset button.
     */
    @FXML
    public void resetTime() {
        if (timeline != null) timeline.stop();
        pomodoroTimer.setMinuteLength(getMinutes());
        pomodoroTimer.reset();
        updateTimerLabel();
        pomodoroToggle.setSelected(false);
        pomodoroToggle.setText("Start");
    }

    /**
     * Sets the controller responsible for switching between Focus and Break panes.
     */
    @Override
    public void setCardController(PomodoroCardController cardController) {
        this.cardController = cardController;
    }
}
