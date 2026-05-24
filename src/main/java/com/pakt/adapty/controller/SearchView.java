package com.pakt.adapty.controller;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class SearchView {

    public TextField searchField;
    public Button searchButton;
    public Button markNextButton;
    public TextField searchCountField;
    public TextField replaceField;
    public Button replaceButton;

    private TextArea textArea;
    private int searchCount = 0;
    private int lastIndex = 0;

    public void initialize() {
        textArea = MainView.currentTab.getTextArea();
        searchCountField.setText(String.valueOf(0));
    }

    public void searchAction() {
        var text = textArea.getText();
        if (!text.isEmpty()) {
            var target = searchField.getText();
            if (!target.isEmpty()) {
                searchCount = 0;
                int index = 0;
                while ((index = text.indexOf(target, index)) != -1) {
                    searchCount++;
                    index += target.length();
                }
                searchCountField.setText(Integer.toString(searchCount));
            }
        }
        textArea.selectRange(0, 0);
        lastIndex = 0;
    }

    public void markNextAction() {
        if (searchCount > 0) {
            var text = textArea.getText();
            if (!text.isEmpty()) {
                var target = searchField.getText();
                if (!target.isEmpty()) {
                    var index = text.indexOf(target, lastIndex);
                    lastIndex = index + target.length();
                    textArea.selectRange(index, lastIndex);
                    searchCount--;
                }
            }
            searchCountField.setText(Integer.toString(searchCount));
        } else lastIndex = 0;
    }

    public void replaceAction() {
        var target = replaceField.getText();
        if (!target.isEmpty()) {
            var selectedTextIndexRange = textArea.getSelection();
            if (selectedTextIndexRange.getEnd() - selectedTextIndexRange.getStart() > 0) {
                textArea.replaceText(selectedTextIndexRange, target);
                lastIndex = textArea.getCaretPosition();
            }
        }
    }
}
