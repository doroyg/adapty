package com.pakt.adapty.controller;

import com.pakt.adapty.util.FileIO;
import javafx.scene.control.TextArea;

import java.nio.file.Paths;

public class ShortcutsView {

    public TextArea shortcutsTextArea;

    public void initialize() {
        var shortcutsFilePath = Paths.get("src/main/resources/data/shortcuts.txt");
        FileIO.createFile(shortcutsFilePath);
        FileIO.readFile(shortcutsFilePath, shortcutsTextArea);

        // TO EDIT SHORTCUTS
//        shortcutsTextArea.setOnKeyPressed(event -> {
//            if (event.getCode() == KeyCode.ALT) {
//                FileIO.writeFile(shortcutsFilePath, shortcutsTextArea);
//            }
//        });
    }
}
