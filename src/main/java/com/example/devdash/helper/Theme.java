package com.example.devdash.helper;

public enum Theme {
    LIGHT("light-theme.css"),
    DARK("dark-theme.css");

    private final String cssFile;

    Theme(String cssFile) {
        this.cssFile = cssFile;
    }

    public String getCssFile() {
        return cssFile;
    }
}
