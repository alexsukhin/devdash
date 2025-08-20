package com.example.devdash.helper;


/**
 * Represents the visual themes available in the application.
 * Each theme corresponds to a CSS file that can be applied to a JavaFX Scene.
 *
 * Author: Alexander Sukhin
 * Version: 04/08/2025
 */
public enum Theme {
    LIGHT("light-theme.css"),
    DARK("dark-theme.css");

    private final String cssFile;

    /**
     * Creates a Theme with the specified CSS file.
     *
     * @param cssFile the name of the CSS file for this theme
     */
    Theme(String cssFile) {
        this.cssFile = cssFile;
    }

    /**
     * Gets the CSS file associated with this theme.
     *
     * @return the CSS file name
     */
    public String getCssFile() {
        return cssFile;
    }
}
