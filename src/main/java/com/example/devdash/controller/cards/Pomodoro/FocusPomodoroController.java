package com.example.devdash.controller.cards.Pomodoro;

public class FocusPomodoroController extends AbstractPomodoroController {
    @Override
    protected int getMinutes() {
        return 25;
    }

    @Override
    protected void onTimerFinish() {
        if (switchHandler != null) {
            switchHandler.switchToBreak();
        }
    }
}