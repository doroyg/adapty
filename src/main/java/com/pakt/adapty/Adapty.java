package com.pakt.adapty;

import com.pakt.adapty.util.UIConfig;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class Adapty extends Application {

    public static Stage stage;
    public static BorderPane root;

    @Override
    public void start(Stage primaryStage) throws IOException {
        stage = primaryStage;
        root = FXMLLoader.load(Objects.requireNonNull(
                Adapty.class.getResource("/views/main.fxml")));
        var scene = new Scene(root);
        var closeApp = new KeyCodeCombination(KeyCode.ESCAPE, KeyCombination.CONTROL_DOWN);
        scene.setOnKeyPressed(event -> {
            if (closeApp.match(event)) Platform.exit();
        });
        stage.setScene(scene);
        stage.setTitle("Adapty");
        stage.setMinWidth(700.0);
        stage.setMinHeight(520.0);
        stage.show();
        stage.centerOnScreen();
        loadIcon();
    }

    private void loadIcon() {
        Path editorSettingsPath;
        try {
            editorSettingsPath = Paths.get(Objects.requireNonNull(
                    Adapty.class.getResource("/data/editor-settings.properties")).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        var editorSettingsProperties = new UIConfig(editorSettingsPath).getProperties();
        var themeLoaded = editorSettingsProperties.getProperty("theme");
        if (themeLoaded.contains("light")) {
            stage.getIcons().add(new Image(Objects.requireNonNull(
                    Adapty.class.getResourceAsStream("/icons/dragon-solid-full_light.svg"))));
        } else if  (themeLoaded.contains("dark")) {
            stage.getIcons().add(new Image(Objects.requireNonNull(
                    Adapty.class.getResourceAsStream("/icons/dragon-solid-full_dark.svg"))));
        } else if  (themeLoaded.contains("default")) {
            stage.getIcons().add(new Image(Objects.requireNonNull(
                    Adapty.class.getResourceAsStream("/icons/dragon-solid-full_default.svg"))));
        }
    }
}
