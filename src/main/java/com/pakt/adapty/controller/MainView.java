package com.pakt.adapty.controller;

import com.pakt.adapty.Adapty;
import com.pakt.adapty.NewTab;
import com.pakt.adapty.util.ExplorerJob;
import com.pakt.adapty.util.FileIO;
import com.pakt.adapty.util.SvgContentMap;
import com.pakt.adapty.util.Theme;
import com.pakt.adapty.util.UIConfig;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class MainView {

    public BorderPane root;
    public TabPane tabPane;

    public MenuItem newMenuItem;
    public MenuItem openMenuItem;
    public MenuItem saveMenuItem;
    public MenuItem saveAsMenuItem;
    public MenuItem closeMenuItem;

    public MenuItem undoMenuItem;
    public MenuItem redoMenuItem;
    public MenuItem cutMenuItem;
    public MenuItem copyMenuItem;
    public MenuItem pasteMenuItem;
    public MenuItem deleteMenuItem;
    public MenuItem clearMenuItem;

    public RadioMenuItem lightThemeMenuItem;
    public RadioMenuItem darkThemeMenuItem;
    public RadioMenuItem defaultThemeMenuItem;
    public CheckMenuItem wrapMenuItem;
    public MenuItem fontMenuItem;

    public MenuItem aboutMenuItem;

    public Button newFileButton;
    public Button openFileButton;
    public Button saveFileButton;
    public Button undoButton;
    public Button redoButton;
    public Button cutButton;
    public Button copyButton;
    public Button pasteButton;
    public Button clearButton;
    public Button printButton;

    public Label notificationLabel;
    public Label encodingLabel;
    public Region footerRegion;

    public static NewTab currentTab;

    public static Stage fontStage;

    private final ToggleGroup themeGroup = new ToggleGroup();
    public static final Path editorSettingsPath = Paths.get(
            "src/main/resources/data/editor-settings.properties");

    private boolean isWrap;
    private Theme selectedTheme;

    public void initialize() {
        var svgContentMap = new SvgContentMap();
        var mapData = svgContentMap.getMapData();
        HBox.setHgrow(footerRegion, Priority.ALWAYS);

        lightThemeMenuItem.setToggleGroup(themeGroup);
        darkThemeMenuItem.setToggleGroup(themeGroup);
        defaultThemeMenuItem.setToggleGroup(themeGroup);
        themeGroup.selectedToggleProperty().addListener(
                (_, _, newValue) -> {
                    var selectedToggle = (RadioMenuItem) newValue;
                    if (selectedToggle == lightThemeMenuItem) selectedTheme = Theme.LIGHT;
                    else if (selectedToggle == darkThemeMenuItem) selectedTheme = Theme.DARK;
                    else if (selectedToggle == defaultThemeMenuItem) selectedTheme = Theme.DEFAULT;
                });

        newFileButton.setGraphic(createSVG(mapData.get("newFileButton")));
        newFileButton.setTooltip(new Tooltip("New"));
        openFileButton.setGraphic(createSVG(mapData.get("openFileButton")));
        openFileButton.setTooltip(new Tooltip("Open"));
        saveFileButton.setGraphic(createSVG(mapData.get("saveFileButton")));
        saveFileButton.setTooltip(new Tooltip("Save"));
        undoButton.setGraphic(createSVG(mapData.get("undoButton")));
        undoButton.setTooltip(new Tooltip("Undo"));
        redoButton.setGraphic(createSVG(mapData.get("redoButton")));
        redoButton.setTooltip(new Tooltip("Redo"));
        cutButton.setGraphic(createSVG(mapData.get("cutButton")));
        cutButton.setTooltip(new Tooltip("Cut"));
        copyButton.setGraphic(createSVG(mapData.get("copyButton")));
        copyButton.setTooltip(new Tooltip("Copy"));
        pasteButton.setGraphic(createSVG(mapData.get("pasteButton")));
        pasteButton.setTooltip(new Tooltip("Paste"));
        clearButton.setGraphic(createSVG(mapData.get("clearButton")));
        clearButton.setTooltip(new Tooltip("Clear"));
        printButton.setGraphic(createSVG(mapData.get("printButton")));
        printButton.setTooltip(new Tooltip("Print"));

        tabPane.getSelectionModel().selectedItemProperty().addListener(
                (_, _, newT) -> {
                    currentTab = (NewTab) newT;
                    var currentTextArea = currentTab.getTextArea();
                    Platform.runLater(currentTextArea::requestFocus);
                    loadFont();
                    currentTextArea.setWrapText(wrapMenuItem.isSelected());
                    updateNotificationLabel();
                }
        );
        wrapMenuItem.selectedProperty().addListener(
                (_, _, newValue) -> {
                    currentTab.getTextArea().setWrapText(newValue);
                    isWrap = newValue;
                }
        );

        createTab();
        loadFont();
        loadEditorSettings();

        setEditorSettingsOnClose();
    }

    private void setKeyEvents(NewTab newTab) {
        var newTabComb = new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN);
        var openFileComb = new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN);
        var saveFileComb = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
        var saveAsFileComb = new KeyCodeCombination(KeyCode.S, KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN);
        var closeTabComb = new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN);
        var nextTabComb = new KeyCodeCombination(KeyCode.TAB, KeyCombination.CONTROL_DOWN);
        var redoComb = new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN);
        var currentTextArea = newTab.getTextArea();
        currentTextArea.setOnKeyPressed(event -> {
            if (newTabComb.match(event)) {
                createTab();
            } else if (openFileComb.match(event)) {
                createTab(FileIO.getFileExplorer(ExplorerJob.OPEN_FILE));
            } else if (saveFileComb.match(event)) {
                saveFileItemAction();
            } else if (saveAsFileComb.match(event)) {
                saveAsFileItemAction();
            } else if (closeTabComb.match(event)) {
                if (tabPane.getTabs().size() > 1) {
                    tabPane.getTabs().remove(tabPane.getSelectionModel().getSelectedItem());
                }
            } else if (nextTabComb.match(event)) {
                var totalTabs = tabPane.getTabs().size();
                if (totalTabs > 1) {
                    var currentIndex = tabPane.getSelectionModel().getSelectedIndex();
                    if (currentIndex < totalTabs - 1) {
                        tabPane.getSelectionModel().select(currentIndex + 1);
                    }
                    tabPane.getSelectionModel().select((currentIndex + 1) % totalTabs);
                }
            } else if (redoComb.match(event)) {
                if (currentTextArea.isRedoable()) {
                    currentTextArea.redo();
                }
            }
        });
    }

    private void loadFont() {
        var savedFontProperties = new UIConfig(FontView.fontSettingsPath).getProperties();
        currentTab.getTextArea().setFont(
                Font.font(savedFontProperties.getProperty("family"),
                        FontWeight.valueOf(savedFontProperties.getProperty("weight")),
                                FontPosture.valueOf(savedFontProperties.getProperty("posture")),
                                        Double.parseDouble(savedFontProperties.getProperty("size"))));
    }

    private void loadEditorSettings() {
        var editorSettingsProperties = new UIConfig(editorSettingsPath).getProperties();

        wrapMenuItem.setSelected(Boolean.parseBoolean(editorSettingsProperties.getProperty("wrap")));
        currentTab.getTextArea().setWrapText(wrapMenuItem.isSelected());

        var themeLoaded = editorSettingsProperties.getProperty("theme");
        if (themeLoaded.contains("light")) {
            selectedTheme = Theme.LIGHT;
            themeGroup.selectToggle(lightThemeMenuItem);
        } else if  (themeLoaded.contains("dark")) {
            selectedTheme = Theme.DARK;
            themeGroup.selectToggle(darkThemeMenuItem);
        } else if  (themeLoaded.contains("default")) {
            selectedTheme = Theme.DEFAULT;
            themeGroup.selectToggle(defaultThemeMenuItem);
        }
        root.getStylesheets().add(
                Objects.requireNonNull(Adapty.class.getResource(selectedTheme.getFilePath())).toExternalForm());
    }

    //
    private void setEditorSettingsOnClose () {
        Adapty.stage.fireEvent(new WindowEvent(Adapty.stage, WindowEvent.WINDOW_CLOSE_REQUEST));
        Adapty.stage.setOnCloseRequest(_ -> {
            var newProp = new UIConfig(editorSettingsPath);
            newProp.addEntry("theme", selectedTheme.getFilePath());
            newProp.addEntry("wrap", String.valueOf(isWrap));
            newProp.setProperties();
        });
    }

    public void newFileItemAction() {
        createTab();
    }

    public void openFileItemAction() {
        createTab(FileIO.getFileExplorer(ExplorerJob.OPEN_FILE));
    }

    public void saveFileItemAction() {
        if (currentTab != null) {
            var currentTabFile = currentTab.getFile();
            if (currentTabFile == null) {
                saveAsFile(FileIO.getFileExplorer(ExplorerJob.SAVE_AS_FILE));
            } else {
                saveFile(currentTabFile);
            }
        }
    }

    public void saveAsFileItemAction() {
        if (currentTab != null) {
            saveAsFile(FileIO.getFileExplorer(ExplorerJob.SAVE_AS_FILE));
        }
    }

    public void closeItemAction() {
        Platform.exit();
    }

    public void undoItemAction() {
        if (currentTab != null) {
            var currentTextArea = currentTab.getTextArea();
            if (currentTextArea.isUndoable()) {
                currentTextArea.undo();
            }
        }
    }

    public void redoItemAction(ActionEvent event) {
        if (currentTab != null) {
            var currentTextArea = currentTab.getTextArea();
            if (currentTextArea.isRedoable()) {
                currentTextArea.redo();
            }
        }
    }

    public void cutItemAction() {
        if (currentTab != null) {
            var currentTextArea = currentTab.getTextArea();
            if (currentTextArea.getSelectedText() != null && !currentTextArea.getSelectedText().isEmpty()) {
                currentTextArea.cut();
            }
        }
    }

    public void copyItemAction() {
        if (currentTab != null) {
            var currentTextArea = currentTab.getTextArea();
            if(currentTextArea.getSelectedText() != null && !currentTextArea.getSelectedText().isEmpty()) {
                currentTextArea.copy();
            }
        }
    }

    public void pasteItemAction() {
        if (currentTab != null) {
            var currentTextArea = currentTab.getTextArea();
            currentTextArea.paste();
        }
    }

    public void deleteItemAction() {
        if(currentTab != null) {
            var currentTextArea = currentTab.getTextArea();
            currentTextArea.deleteText(currentTextArea.getSelection());
        }
    }

    public void clearItemAction() {
        if (currentTab != null) {
            var currentTextArea = currentTab.getTextArea();
            currentTextArea.clear();
        }
    }

    public void smileysItemAction() {
        try {
            var smileysStage = new Stage();
            AnchorPane smileysView = FXMLLoader.load(Objects.requireNonNull(
                    Adapty.class.getResource("/views/smileys.fxml")));
            var scene = new Scene(smileysView);
            smileysStage.setResizable(false);
            smileysStage.initOwner(Adapty.stage);
            smileysStage.initStyle(StageStyle.UTILITY);
            smileysStage.initModality(Modality.WINDOW_MODAL);
            smileysStage.setScene(scene);
            smileysStage.show();
            smileysStage.centerOnScreen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void dateTimeAction() {
        if (currentTab != null) {
            var currentTextArea = currentTab.getTextArea();
            var caretPos = currentTextArea.getCaretPosition();
            var dateTime = LocalDateTime.now();
            var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            var dateTimeText = dateTime.format(formatter);
            var selectionIndex = currentTextArea.getSelection();
            if (selectionIndex.getLength() > 0) {
                currentTextArea.replaceSelection(dateTimeText);
            } else {
                currentTextArea.insertText(caretPos, dateTime.format(formatter));
            }
        }
    }

    public void searchItemAction() {
        try {
            var searchStage = new Stage();
            AnchorPane searchView = FXMLLoader.load(Objects.requireNonNull(
                    Adapty.class.getResource("/views/search.fxml")));
            var scene = new Scene(searchView);
            searchStage.setResizable(false);
            searchStage.initOwner(Adapty.stage);
            searchStage.initStyle(StageStyle.UTILITY);
            searchStage.initModality(Modality.WINDOW_MODAL);
            searchStage.setScene(scene);
            searchStage.show();
            searchStage.centerOnScreen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void fontAction() {
        showFontView();
    }

    public void shortcutsItemAction() {
        try {
            var shortcutsStage = new Stage();
            AnchorPane shortcutsView = FXMLLoader.load(Objects.requireNonNull(
                    Adapty.class.getResource("/views/shortcuts.fxml")));
            var scene = new Scene(shortcutsView);
            shortcutsStage.setResizable(false);
            shortcutsStage.initOwner(Adapty.stage);
            shortcutsStage.initStyle(StageStyle.UTILITY);
            shortcutsStage.initModality(Modality.WINDOW_MODAL);
            shortcutsStage.setScene(scene);
            shortcutsStage.show();
            shortcutsStage.centerOnScreen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void aboutItemAction(ActionEvent event) {
        try {
            var aboutStage = new Stage();
            AnchorPane aboutView = FXMLLoader.load(Objects.requireNonNull(
                    Adapty.class.getResource("/views/about.fxml")));
            var scene = new Scene(aboutView);
            aboutStage.setResizable(false);
            aboutStage.initOwner(Adapty.stage);
            aboutStage.initStyle(StageStyle.UTILITY);
            aboutStage.initModality(Modality.WINDOW_MODAL);
            aboutStage.setScene(scene);
            aboutStage.show();
            aboutStage.centerOnScreen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void newButtonAction() {
        createTab();
    }

    public void openButtonAction() {
        createTab(FileIO.getFileExplorer(ExplorerJob.OPEN_FILE));
    }

    public void saveButtonAction() {
        if (currentTab != null) {
            var currentTabFile = currentTab.getFile();
            if (currentTabFile == null) {
                saveAsFile(FileIO.getFileExplorer(ExplorerJob.SAVE_AS_FILE));
            } else {
                saveFile(currentTabFile);
            }
        }
    }

    public void undoButtonAction(ActionEvent event) {
        if (currentTab != null) {
            var currentTextArea = currentTab.getTextArea();
            if (currentTextArea.isUndoable()) {
                currentTextArea.undo();
            }
        }
    }

    public void redoButtonAction(ActionEvent event) {
        if (currentTab != null) {
            var currentTextArea = currentTab.getTextArea();
            if (currentTextArea.isRedoable()) {
                currentTextArea.redo();
            }
        }
    }

    public void cutButtonAction() {
        if (currentTab != null) {
            var currentTextArea = currentTab.getTextArea();
            if (currentTextArea.getSelectedText() != null && !currentTextArea.getSelectedText().isEmpty()) {
                currentTextArea.cut();
            }
        }
    }

    public void copyButtonAction() {
        if (currentTab != null) {
            var currentTextArea = currentTab.getTextArea();
            if(currentTextArea.getSelectedText() != null && !currentTextArea.getSelectedText().isEmpty()) {
                currentTextArea.copy();
            }
        }
    }

    public void pasteButtonAction() {
        if (currentTab != null) {
            var currentTextArea = currentTab.getTextArea();
            currentTextArea.paste();
        }
    }

    public void clearButtonAction() {
        try {
            var clearStage = new Stage();
            AnchorPane clearView = FXMLLoader.load(Objects.requireNonNull(
                    Adapty.class.getResource("/views/clear.fxml")));
            var scene = new Scene(clearView);
            clearStage.setResizable(false);
            clearStage.initOwner(Adapty.stage);
            clearStage.initStyle(StageStyle.UTILITY);
            clearStage.initModality(Modality.WINDOW_MODAL);
            clearStage.setScene(scene);
            clearStage.show();
            clearStage.centerOnScreen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void printButtonAction() {
        var textContent = currentTab.getTextArea().getText();
        if (textContent != null && !textContent.isEmpty()) {
            printButton.setOnAction(_ -> {
                if (Desktop.isDesktopSupported()) {
                    var desktop = Desktop.getDesktop();
                    if (desktop.isSupported(Desktop.Action.PRINT)) {
                        var pathToHome = Paths.get(System.getProperty("user.home"));
                        try {
                            var tempFile = File.createTempFile("temp", ".txt", pathToHome.toFile());
                            try (var fileWriter = new FileWriter(tempFile)) {
                                fileWriter.write(textContent);
                            }
                            desktop.print(tempFile);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        notificationLabel.setText("Desktop print action not supported.");
                    }
                } else {
                    notificationLabel.setText("Desktop access not supported.");
                }
            });
        }
    }

    private void createTab(File file) {
        if (file != null) {
            if (FileIO.isTextFile(file.toPath())) {
                if (currentTab.getFile() == null) {
                    if (currentTab.getTextArea().getText().isEmpty()) {
                        currentTab.setFile(file);
                        currentTab.populateTextArea();
                    } else initNewTab(file);
                } else initNewTab(file);
                notificationLabel.setText(file.getAbsolutePath());
            }
        }
        currentTab.getTextArea().requestFocus();
        currentTab.setOnCloseRequest(event -> {
            if (tabPane.getTabs().size() == 1) event.consume();
        });
    }

    private void initNewTab(File file) {
        var newTab = new NewTab();
        newTab.setFile(file);
        newTab.populateTextArea();
        tabPane.getTabs().add(newTab);
        tabPane.getSelectionModel().select(newTab);
        currentTab = newTab;
        setKeyEvents(currentTab);
    }

    private void createTab() {
        var newTab = new NewTab();
        newTab.setText("New Tab");
        tabPane.getTabs().add(newTab);
        tabPane.getSelectionModel().select(newTab);
        currentTab = newTab;
        currentTab.getTextArea().requestFocus();
        currentTab.setOnCloseRequest(event -> {
            if (tabPane.getTabs().size() == 1) event.consume();
        });
        setKeyEvents(currentTab);
    }

    private void saveAsFile(File file) {
        if (file != null) {
            FileIO.writeFile(file.toPath(), currentTab.getTextArea());
            currentTab.setFile(file);
            notificationLabel.setText(file.getAbsolutePath());
        }
    }

    private void saveFile(File file) {
        FileIO.writeFile(file.toPath(), currentTab.getTextArea());
        if (currentTab.getFile() != null) {
            notificationLabel.setText(file.getAbsolutePath());
        }
    }

    private Region createSVG(String data) {
        var svgPath = new SVGPath();
        var svgShape = new Region();
        var svgSize = 16.0;
        svgPath.setContent(data);

        svgShape.setShape(svgPath);
        svgShape.setMinSize(svgSize, svgSize);
        svgShape.setPrefSize(svgSize, svgSize);
        svgShape.setMaxSize(svgSize, svgSize);
        svgShape.setBackground(Background.fill(Color.valueOf("#22252b")));
        return svgShape;
    }

    private void showFontView() { // WORK ON THIS -> FONT VIEW
        try {
            fontStage = new Stage();
            AnchorPane fontView = FXMLLoader.load(Objects.requireNonNull(
                    Adapty.class.getResource("/views/font.fxml")));
            var scene = new Scene(fontView);
            fontStage.setResizable(false);
            fontStage.initOwner(Adapty.stage);
            fontStage.initStyle(StageStyle.UTILITY);
            fontStage.initModality(Modality.WINDOW_MODAL);
            fontStage.setScene(scene);
            fontStage.show();
            fontStage.centerOnScreen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateNotificationLabel() {
        var file = currentTab.getFile();
        if (file != null) {
            if (FileIO.isTextFile(file.toPath())) {
                notificationLabel.setText(file.getAbsolutePath());
            }
        } else {
            notificationLabel.setText("");
        }
    }
}
