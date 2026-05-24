package com.pakt.adapty.util;

public enum ExplorerJob {

    OPEN_FILE("Open File"),
    SAVE_AS_FILE("Save As File"),
    SAVE_FILE("Save File");

    private final String job;

    ExplorerJob(String job) {
        this.job = job;
    }

    public String getJob() {
        return job;
    }
}
