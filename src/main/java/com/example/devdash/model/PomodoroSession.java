package com.example.devdash.model;

public class PomodoroSession {

    private static final int SECONDS_IN_MINUTE = 60;

    private int minuteLength;
    private int minute;
    private int second;

    public PomodoroSession(int minuteLength) {
        this.minuteLength = minuteLength;
        this.reset();
    }

    public String getCurrentTime() {
        return String.format("%02d:%02d", minute, second);
    }

    public void oneSecondPassed() {
        if (isFinished()) return;

        if (second == 0) {
            if (minute > 0) {
                minute--;
                second = SECONDS_IN_MINUTE - 1;
            }
        } else {
                second--;
        }
    }

    public boolean isFinished() {
        return minute == 0 && second == 0;
    }

    public void reset() {
        minute = minuteLength;
        second = 0;
    }

    public void setMinuteLength(int minute) {
        minuteLength = minute;
    }

    public int getMinuteLength() {
        return minuteLength;
    }
}
