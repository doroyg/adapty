package com.pakt.adapty.controller;

import javafx.scene.control.Button;

public class ClearView {

    public Button clearAllTextsButton;

    public void Initialize() {

    }

    public void clearAllTextsAction() {
        var currentTab = MainView.currentTab;
        if (currentTab != null) {
            var currentTextArea = currentTab.getTextArea();
            currentTextArea.clear();
        }
    }
}
