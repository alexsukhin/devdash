package com.example.devdash.controller.cards.Pomodoro;

public class BreakPomodoroController extends AbstractPomodoroController {
    @Override
    protected int getMinutes() {
        return 5;
    }

    @Override
    protected void onTimerFinish() {
        if (switchHandler != null) {
            switchHandler.switchToFocus();
        }
    }
}

