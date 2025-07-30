package com.github.arthurdeka.cedromoderndock.controller;

import com.github.arthurdeka.cedromoderndock.App;
import com.github.arthurdeka.cedromoderndock.model.*;
import com.github.arthurdeka.cedromoderndock.util.Logger;
import com.github.arthurdeka.cedromoderndock.util.SaveAndLoadDockSettings;
import com.github.arthurdeka.cedromoderndock.util.WindowsIconHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.github.arthurdeka.cedromoderndock.util.UIUtils.setStageIcon;

public class DockController {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private HBox hBoxContainer;

    private DockModel model;
    private Stage stage;

    // variables for the enableDrag function
    private double xOffset = 0;
    private double yOffset = 0;

    // Run when FXML is loaded
    public void handleInitialization() {
        model = SaveAndLoadDockSettings.load();

        enableDrag();
        updateDockUI();
    }

    // enables dock drag effect
    private void enableDrag() {
        rootPane.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        rootPane.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        // saves the dock position on the model
        rootPane.setOnMouseReleased(event -> {
            model.setDockPosition(stage.getX(), stage.getY());
        });
    }

    public void addDockItem(DockItem item) {
        model.addItem(item);
    }

    public void removeDockItem(int index) {
        model.removeItem(index);
    }

    public List<DockItem> getDockItems() {
        return model.getItems();
    }

    public void swapItems(int firstItemIdx, int secondItemIdx) {
        model.swapItems(firstItemIdx, secondItemIdx);
        updateDockUI();
    }

    /* this method updates the DockView (actual rendered Dock) style and saves the changes */
    public void updateDockUI() {
        hBoxContainer.getChildren().clear();
        hBoxContainer.setSpacing(getDockIconsSpacing());
        hBoxContainer.setStyle("-fx-background-color: rgba(" + model.getDockColorRGB() + " " + model.getDockTransparency() + ");" + "-fx-background-radius: " + model.getDockBorderRounding() + ";");

        for (DockItem item : model.getItems()) {
            Button button = createButton(item);
            hBoxContainer.getChildren().add(button);
        }

        // resize DockView window to account for DockItem additions or removing
        stage.sizeToScene();
        // sets DockView to the saved location on screen
        stage.setX(model.getDockPositionX());
        stage.setY(model.getDockPositionY());
        saveChanges();
    }

    private Button createButton(DockItem item) {

        if (item instanceof DockSettingsItemModel) {
            Image icon = new Image(getClass().getResourceAsStream(item.getPath()));
            ImageView imageView = new ImageView(icon);
            imageView.setFitWidth(model.getIconsSize());
            imageView.setFitHeight(model.getIconsSize());

            Button button = new Button(item.getLabel());
            button.getStyleClass().add("dock-button");
            button.setGraphic(imageView);
            button.setOnAction(e -> openSettingsWindow());
            return button;

        } else if (item instanceof DockWindowsModuleItemModel) {
            Image icon = new Image(getClass().getResourceAsStream(item.getPath()));
            ImageView imageView = new ImageView(icon);
            imageView.setFitWidth(model.getIconsSize());
            imageView.setFitHeight(model.getIconsSize());

            Button button = new Button(item.getLabel());
            button.getStyleClass().add("dock-button");
            button.setGraphic(imageView);
            button.setOnAction(e -> item.performAction());
            return button;

        // Logic for DockProgramItemModel runs on a background thread
        } else if (item instanceof DockProgramItemModel) {
            Path iconPath = WindowsIconHandler.getCachedIconPath(item.getPath());

            // if file does not exist
            if (Files.notExists(iconPath)) {
                Logger.error("DockController - createButton - path for cached icon not found");
            }

            Image icon = new Image(iconPath.toUri().toString());
            ImageView imageView = new ImageView(icon);
            imageView.setFitWidth(model.getIconsSize());
            imageView.setFitHeight(model.getIconsSize());

            Button button = new Button(item.getLabel());
            button.getStyleClass().add("dock-button");
            imageView.setFitWidth(model.getIconsSize());
            imageView.setFitHeight(model.getIconsSize());
            button.setGraphic(imageView);

            button.setOnAction(e -> item.performAction());
            return button;

        } else {
            return null;
        }
    }

    private void openSettingsWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/DockSettingsView.fxml"));
            Parent root = loader.load();

            SettingsController settingsController = loader.getController();
            settingsController.setDockController(this);
            settingsController.handleInitialization();

            Stage stage = new Stage();
            stage.setTitle("Settings Window");
            setStageIcon(stage);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void setDockIconsSize(int iconsSize) {
        model.setIconsSize(iconsSize);
        updateDockUI();
    }

    public int getDockIconsSize() {
        return model.getIconsSize();
    }

    public void setDockIconsSpacing(int spacingValue) {
        model.setSpacingBetweenIcons(spacingValue);
        updateDockUI();
    }

    public int getDockIconsSpacing() {
        return model.getSpacingBetweenIcons();
    }

    public int getDockTransparency() {
        int intValue = (int) (model.getDockTransparency() * 100);
        return intValue;
    }

    public void setDockTransparency(int value) {
        double doubleValue = (double) value / 100;
        model.setDockTransparency(doubleValue);
        updateDockUI();
    }

    public void setDockBorderRounding(int value) {
        model.setDockBorderRounding(value);
        updateDockUI();

    }

    public int getDockBorderRounding() {
        return model.getDockBorderRounding();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public String getDockColorRGB() {
        return model.getDockColorRGB();
    }

    public void setDockColorRGB(String value) {
        model.setDockColorRGB(value);
        updateDockUI();
    }

    public void saveChanges() {
        model.saveChanges();
    }
}