package com.example.devdash.controller.cards.Pomodoro;

import com.example.devdash.model.PomodoroSession;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import javafx.scene.control.ToggleButton;
import javafx.util.Duration;

public abstract class AbstractPomodoroController implements PomodoroPaneController {

    @FXML private Label timer;
    @FXML private ToggleButton pomodoroToggle;

    protected Timeline timeline;
    protected PomodoroSession pomodoroSession;
    protected PomodoroSwitchHandler switchHandler;


    protected abstract int getMinutes();
    protected abstract void onTimerFinish();

    @FXML
    public void initialize() {
        pomodoroSession = new PomodoroSession(getMinutes());
        setupTimeline();
        setupToggleBehavior();
        updateTimerLabel();
    }

    private void setupTimeline() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> tick()));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

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

    @FXML
    public void resetTime() {
        this.reset();
    }

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

    private void updateTimerLabel() {
        timer.setText(pomodoroSession.getCurrentTime());
    }

    public void setSwitchHandler(PomodoroSwitchHandler handler) {
        this.switchHandler = handler;
    }
}