package com.pakt.adapty.controller;

import com.pakt.adapty.Adapty;
import com.pakt.adapty.util.FileIO;
import javafx.scene.control.TextArea;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class ShortcutsView {

    public TextArea shortcutsTextArea;

    public void initialize() {
        Path shortcutsFilePath = null;
        try {
            shortcutsFilePath = Paths.get(Objects.requireNonNull(
                    Adapty.class.getResource("/data/shortcuts.txt")).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        FileIO.createFile(shortcutsFilePath);
        FileIO.readFile(shortcutsFilePath, shortcutsTextArea);
    }
}
