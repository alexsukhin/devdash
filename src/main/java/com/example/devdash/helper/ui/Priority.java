package com.example.devdash.helper.ui;

/**
 * Represents the priorities used for task nodes.
 *
 * Author: Alexander Sukhin
 * Version: 04/08/2025
 */
public enum Priority {
    LOW ("Low", 0), 
    MEDIUM("Medium", 1),
    HIGH("High", 2);

    private final String label;
    private final int value;

    /**
     * Constructs a Prority with the given label and numeric value.
     *
     * @param label human-readable label
     * @param value numeric representation of the priority
     */
    Priority(String label, int value)  {
        this.label = label;
        this.value = value;
    }

    /**
     * @return The label of the priority
     */
    public String getLabel() { return label; }

    /**
     * @return The numeric value of the priority
     */
    public int getValue() { return value; }

    /**
     * Retrieves the corresponding Priority enum for the given integer value.
     *
     * @param value Integer value
     * @return The matching Priority
     */
    public static Priority getPriority(int value) {
        for (Priority priority : Priority.values()) {
            if (priority.getValue() == value) {
                return priority;
            }
        }
        return LOW;
    }

    /**
     * @return The label of the priority
     */
    @Override
    public String toString() {
        return label;
    }
}
