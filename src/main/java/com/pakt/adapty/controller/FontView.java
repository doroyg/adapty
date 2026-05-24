package com.pakt.adapty.controller;

import com.pakt.adapty.util.UIConfig;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class FontView {

    public ListView<String> fontFamilyList;
    public ListView<FontWeight> fontWeightList;
    public ListView<Double> fontSizeList;
    public Label sampleLabel;
    public Button applyButton;
    public CheckBox fontPosture;

    private ObservableList<String> fontFamilies;
    private ObservableList<FontWeight> fontWeights;
    private ObservableList<Double> fontSizes;
    private Double[] sizes;

    private String currentFontFamily;
    private FontWeight currentFontWeight;
    private double currentFontSize;
    private FontPosture currentFontPosture;

    public static final Path fontSettingsPath = Paths.get("src/main/resources/data/font-settings.properties");

    public void initialize() {
        //getting data for listviews
        fontFamilies = FXCollections.observableArrayList(Font.getFamilies());
        fontWeights = FXCollections.observableArrayList(Arrays.asList(FontWeight.values()));
        sizes = getSizes();
        fontSizes = FXCollections.observableArrayList(Arrays.asList(sizes));

        //populate listviews
        fontFamilyList.setItems(fontFamilies);
        fontWeightList.setItems(fontWeights);
        fontSizeList.setItems(fontSizes);

        // var userFont = getUserFont(); // load from config
        var savedFontProperties = new UIConfig(fontSettingsPath).getProperties();
        fontFamilyList.getSelectionModel().select(savedFontProperties.getProperty("family"));
        fontFamilyList.scrollTo(savedFontProperties.getProperty("family"));
        fontWeightList.getSelectionModel().select(FontWeight.valueOf(savedFontProperties.getProperty("weight")));
        fontWeightList.scrollTo(FontWeight.valueOf(savedFontProperties.getProperty("weight")));
        fontPosture.setSelected(!savedFontProperties.getProperty("posture").equals("REGULAR"));
        fontSizeList.getSelectionModel().select(Double.parseDouble(savedFontProperties.getProperty("size")));
        fontSizeList.scrollTo(Double.parseDouble(savedFontProperties.getProperty("size")));

        // assign the loaded font configs to current font
        currentFontFamily = fontFamilyList.getSelectionModel().getSelectedItem();
        currentFontWeight = fontWeightList.getSelectionModel().getSelectedItem();
        currentFontPosture = (fontPosture.isSelected()) ? FontPosture.ITALIC : FontPosture.REGULAR;
        currentFontSize = fontSizeList.getSelectionModel().getSelectedItem();
        sampleLabel.setFont(Font.font(currentFontFamily, currentFontWeight, currentFontPosture, currentFontSize));
        MainView.currentTab.getTextArea().setFont(
                Font.font(currentFontFamily, currentFontWeight, currentFontPosture, currentFontSize));

        fontFamilyList.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    currentFontFamily = newValue;
                    sampleLabel.setFont(Font.font(currentFontFamily, currentFontWeight, currentFontPosture, currentFontSize));
                });
        fontWeightList.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    currentFontWeight = newValue;
                    sampleLabel.setFont(Font.font(currentFontFamily, currentFontWeight, currentFontPosture, currentFontSize));
                });
        fontSizeList.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue > 23.0) currentFontSize = 23.0;
                    else currentFontSize = newValue;
                    sampleLabel.setFont(Font.font(currentFontFamily, currentFontWeight, currentFontPosture, currentFontSize));
                });
        fontPosture.selectedProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue) currentFontPosture = FontPosture.ITALIC;
                    else currentFontPosture = FontPosture.REGULAR;
                    sampleLabel.setFont(Font.font(currentFontFamily, currentFontWeight, currentFontPosture, currentFontSize));
                });
    }

    private Double[]  getSizes() {
        var temp = new Double[36];
        for (int i = 0; i < temp.length; i++) {
            temp[i] = i + 7.0;
        }
        return temp;
    }

    private Font getUserFont() {
        return null;
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