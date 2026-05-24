package com.pakt.adapty;

import com.pakt.adapty.util.FileIO;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;

import java.io.File;

public class NewTab extends Tab {

    private final StackPane pane;
    private final TextArea textArea;
    private final ProgressBar progressBar;

    private File file;

    public NewTab() {
        pane = new StackPane();
        textArea = new TextArea();
        progressBar = new ProgressBar();
        init();
    }

    private void init() {
        progressBar.setVisible(false);
        pane.getChildren().addAll(textArea, progressBar);
        setContent(pane);
    }

    public void setFile(File newFile) {
        file = newFile;
        setText(file.getName());
    }

    public void populateTextArea() {
        FileIO.readFile(file.toPath(), textArea);
    }

    public File getFile() {
        return file;
    }

    public StackPane getPane() {
        return pane;
    }

    public TextArea getTextArea() {
        return textArea;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }
}
