package com.pakt.adapty.util;

import java.nio.file.Path;
import java.util.Properties;

public class UIConfig {

    private final Path path;
    private final Properties properties;

    public UIConfig(Path path) {
        properties = new Properties();
        this.path = path;
        FileIO.createFile(path);
    }


    public void addEntry(String key, String value) {
        properties.setProperty(key, value);
    }

    public void setProperties() {
        FileIO.writeProperties(properties, path);
    }

    public Properties getProperties() {
        return FileIO.readProperties(path);
    }
}
