package com.example.devdash.model;

public class PomodoroSession {
    private int minute;
    private int second;

    public PomodoroSession(int hour, int minute, int second) {
        this.minute = minute;
        this.second = second;
    }

    public void oneSecondPassed() {

        if (second == 0) {
            minute--;
            second = 60;
            if (minute == 0) {
                System.out.println("Timer finished");
            }
        }


        second--;

    }
}
