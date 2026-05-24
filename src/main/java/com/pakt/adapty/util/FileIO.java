package com.pakt.adapty.util;

import com.pakt.adapty.Adapty;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class FileIO {

    private FileIO() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isTextFile(Path path) {
        if (Files.isReadable(path) && Files.isRegularFile(path)) {
            try {
                byte[] bytes = Files.readAllBytes(path);
                StandardCharsets.UTF_8.newDecoder().decode(ByteBuffer.wrap(bytes));
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    public static void readFile(Path path, TextArea textArea) {
        var task = new Task<Void>() {
            @Override
            protected Void call() {
                try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        final String currentLine = line;
                        Platform.runLater(() -> {
                            textArea.appendText(currentLine + "\n");
                        });
                        Thread.sleep(10);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return null;
            }
        };
        var thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    // for very large string
    public static String[] splitStringBySize(String text, int size) {
        if (text == null || size <= 0) return new String[0];

        int arraySize = (int) Math.ceil((double) text.length() / size);
        String[] results = new String[arraySize];

        for (int i = 0; i < arraySize; i++) {
            int start = i * size;
            int end = Math.min(text.length(), (i + 1) * size);
            results[i] = text.substring(start, end);
        }
        return results;
    }

    public static void writeFile(Path filePath, TextArea textArea) {
        var content = textArea.getText();
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
                    writer.write(content);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return null;
            }
        };
        var thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public static void writeConfig(Map<String, String> map, Path path) {
        var properties = new Properties();
        try (var outputStream = new FileOutputStream(path.toFile(), false)) {
            map.forEach(properties::setProperty);
            properties.store(outputStream, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Properties readConfig(Path path) {
        var properties = new Properties();
        try (var inputStream = new FileInputStream(path.toFile())) {
            properties.load(inputStream);
            return properties;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static File getFileExplorer(ExplorerJob job) {
        var fileChooser = new FileChooser();
        fileChooser.setTitle(job.getJob());
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        return switch (job) {
            case OPEN_FILE -> fileChooser.showOpenDialog(Adapty.stage);
            case SAVE_AS_FILE, SAVE_FILE -> fileChooser.showSaveDialog(Adapty.stage);
        };
    }

    public static void writeData(Path path, Map<String, String> data) {
        try (var outputStream = new ObjectOutputStream(new FileOutputStream(path.toString()))) {
            outputStream.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, String> readData(Path path) {
        try (var inputStream = new ObjectInputStream(new FileInputStream(path.toString()))) {
            return castToMap(inputStream.readObject());
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    public static Map<String, String> castToMap(Object obj) {
        Map<String, String> result = new HashMap<>();
        if (obj instanceof Map<?, ?>) {
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) obj).entrySet()) {
                if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
                    result.put((String) entry.getKey(), (String) entry.getValue());
                }
            }
        }
        return result;
    }

    public static void createFile(Path path){
        if (Files.notExists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void writeProperties(Properties properties, Path path) {
        try (OutputStream output = new FileOutputStream(path.toFile())) {
            properties.store(output, "");
        } catch (IOException e) {
            throw new  RuntimeException(e);
        }
    }

    public static Properties readProperties(Path path) {
        try (InputStream input = new FileInputStream(path.toFile())) {
            var properties = new Properties();
            properties.load(input);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
