package com.example.devdash.model.pomodoro;

/**
 * Represents a single Pomodoro timer session.
 *
 * Author: Alexander Sukhin
 * Version: 04/08/2025
 */
public class PomodoroTimer {

    // Constant for number of seconds in one minute
    private static final int SECONDS_IN_MINUTE = 60;

    // Length of the session in minutes (e.g., 25 for a 25-minute Pomodoro)
    private int minuteLength;

    // Current timer minutes and seconds
    private int minute;
    private int second;

    /**
     * Creates a PomodoroSession with a given length in minutes.
     *
     * @param minuteLength The length of the Pomodoro session in minutes.
     */
    public PomodoroTimer(int minuteLength) {
        this.minuteLength = minuteLength;
        this.reset();
    }

    /**
     * Returns the current countdown time as a string formatted "MM:SS".
     *
     * @return The current time left in the session.
     */
    public String getCurrentTime() {
        return String.format("%02d:%02d", minute, second);
    }

    /**
     * Decrements the timer by one second.
     */
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

    /**
     * Checks if the Pomodoro session has finished.
     *
     * @return True if the session time is 00:00, false otherwise.
     */
    public boolean isFinished() {
        return minute == 0 && second == 0;
    }

    /**
     * Resets the timer to the original session length.
     */
    public void reset() {
        minute = minuteLength;
        second = 0;
    }

    public void setMinuteLength(int minutes) {
        this.minuteLength = minutes;
    }
}
