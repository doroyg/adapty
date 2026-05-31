package com.pakt.adapty.controller;

import com.pakt.adapty.Adapty;
import com.pakt.adapty.util.UIConfig;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;

public class FontView {

    public ListView<String> fontFamilyList;
    public ListView<FontWeight> fontWeightList;
    public ListView<Double> fontSizeList;
    public Label sampleLabel;
    public Button applyButton;
    public CheckBox fontPosture;

    private String currentFontFamily;
    private FontWeight currentFontWeight;
    private double currentFontSize;
    private FontPosture currentFontPosture;

    public static Path fontSettingsPath;

    static {
        try {
            fontSettingsPath = Paths.get(Objects.requireNonNull(
                    Adapty.class.getResource("/data/font-settings.properties")).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void initialize() {
        var fontFamilies = FXCollections.observableArrayList(Font.getFamilies());
        var fontWeights = FXCollections.observableArrayList(Arrays.asList(FontWeight.values()));
        var sizes = getSizes();
        var fontSizes = FXCollections.observableArrayList(Arrays.asList(sizes));

        fontFamilyList.setItems(fontFamilies);
        fontWeightList.setItems(fontWeights);
        fontSizeList.setItems(fontSizes);

        var savedFontProperties = new UIConfig(fontSettingsPath).getProperties();
        fontFamilyList.getSelectionModel().select(savedFontProperties.getProperty("family"));
        fontFamilyList.scrollTo(savedFontProperties.getProperty("family"));
        fontWeightList.getSelectionModel().select(FontWeight.valueOf(savedFontProperties.getProperty("weight")));
        fontWeightList.scrollTo(FontWeight.valueOf(savedFontProperties.getProperty("weight")));
        fontPosture.setSelected(!savedFontProperties.getProperty("posture").equals("REGULAR"));
        fontSizeList.getSelectionModel().select(Double.parseDouble(savedFontProperties.getProperty("size")));
        fontSizeList.scrollTo(Double.parseDouble(savedFontProperties.getProperty("size")));

        currentFontFamily = fontFamilyList.getSelectionModel().getSelectedItem();
        currentFontWeight = fontWeightList.getSelectionModel().getSelectedItem();
        currentFontPosture = (fontPosture.isSelected()) ? FontPosture.ITALIC : FontPosture.REGULAR;
        currentFontSize = fontSizeList.getSelectionModel().getSelectedItem();
        sampleLabel.setFont(Font.font(currentFontFamily, currentFontWeight, currentFontPosture, currentFontSize));
        MainView.currentTab.getTextArea().setFont(
                Font.font(currentFontFamily, currentFontWeight, currentFontPosture, currentFontSize));

        fontFamilyList.getSelectionModel().selectedItemProperty().addListener(
                (_, _, newValue) -> {
                    currentFontFamily = newValue;
                    sampleLabel.setFont(Font.font(currentFontFamily, currentFontWeight, currentFontPosture, currentFontSize));
                });
        fontWeightList.getSelectionModel().selectedItemProperty().addListener(
                (_, _, newValue) -> {
                    currentFontWeight = newValue;
                    sampleLabel.setFont(Font.font(currentFontFamily, currentFontWeight, currentFontPosture, currentFontSize));
                });
        fontSizeList.getSelectionModel().selectedItemProperty().addListener(
                (_, _, newValue) -> {
                    currentFontSize = newValue;
                    if (newValue > 23.0) {
                        sampleLabel.setFont(Font.font(currentFontFamily, currentFontWeight, currentFontPosture, 23.0));
                    } else {
                        sampleLabel.setFont(Font.font(currentFontFamily, currentFontWeight, currentFontPosture, currentFontSize));
                    }
                });
        fontPosture.selectedProperty().addListener(
                (_, _, newValue) -> {
                    if (newValue) currentFontPosture = FontPosture.ITALIC;
                    else currentFontPosture = FontPosture.REGULAR;
                    sampleLabel.setFont(Font.font(currentFontFamily, currentFontWeight, currentFontPosture, currentFontSize));
                });
    }

    private Double[]  getSizes() {
        var temp = new Double[32];
        for (int i = 0; i < temp.length; i++) {
            temp[i] = i + 9.0;
        }
        return temp;
    }

    public void applyButtonAction() {
        MainView.currentTab.getTextArea().setFont(
                Font.font(currentFontFamily, currentFontWeight, currentFontPosture, currentFontSize));
        var newFontConfig = new UIConfig(fontSettingsPath);
        newFontConfig.addEntry("family", currentFontFamily);
        newFontConfig.addEntry("weight", currentFontWeight.toString());
        newFontConfig.addEntry("posture", currentFontPosture.toString());
        newFontConfig.addEntry("size", String.valueOf(currentFontSize));
        newFontConfig.setProperties();
    }
}