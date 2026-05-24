package com.pakt.adapty.util;

public enum Theme {

    LIGHT("/styles/light.css"),
    DARK("/styles/dark.css"),
    DEFAULT("/styles/default.css"),;

    private final String filePath;

    Theme(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }
}
