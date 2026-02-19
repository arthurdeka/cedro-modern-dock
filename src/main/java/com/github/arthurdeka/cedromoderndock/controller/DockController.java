package com.github.arthurdeka.cedromoderndock.controller;

import com.github.arthurdeka.cedromoderndock.App;
import com.github.arthurdeka.cedromoderndock.model.*;
import com.github.arthurdeka.cedromoderndock.util.Logger;
import com.github.arthurdeka.cedromoderndock.util.SaveAndLoadDockSettings;
import com.github.arthurdeka.cedromoderndock.util.WindowsIconHandler;
import com.github.arthurdeka.cedromoderndock.util.NativeWindowUtils;
import com.github.arthurdeka.cedromoderndock.view.WindowPreviewPopup;
import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.util.Duration;
import javafx.geometry.Bounds;
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
    private WindowPreviewPopup windowPreviewPopup;
    private PauseTransition hideTimer;

    // variables for the enableDrag function
    private double xOffset = 0;
    private double yOffset = 0;

    // Run when FXML is loaded
    public void handleInitialization() {
        model = SaveAndLoadDockSettings.load();

        windowPreviewPopup = new WindowPreviewPopup();
        // Increased delay to allow moving mouse from dock icon to popup
        hideTimer = new PauseTransition(Duration.millis(500));
        hideTimer.setOnFinished(e -> windowPreviewPopup.hide());

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
            if (button != null) {
                hBoxContainer.getChildren().add(button);
            }
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
                return null;
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

            setupHoverPreview(button, (DockProgramItemModel) item, icon);

            return button;

        } else {
            return null;
        }
    }

    private void setupHoverPreview(Button button, DockProgramItemModel item, Image icon) {
        PauseTransition showDelay = new PauseTransition(Duration.millis(300));

        button.setOnMouseEntered(e -> {
            hideTimer.stop();
            showDelay.setOnFinished(ev -> showWindowPreview(button, item, icon));
            showDelay.playFromStart();
        });

        button.setOnMouseExited(e -> {
            showDelay.stop();
            hideTimer.playFromStart();
        });
    }

    private void showWindowPreview(Button button, DockProgramItemModel item, Image icon) {
        Task<List<NativeWindowUtils.WindowInfo>> task = new Task<>() {
            @Override
            protected List<NativeWindowUtils.WindowInfo> call() throws Exception {
                return NativeWindowUtils.getOpenWindows(item.getPath());
            }
        };

        task.setOnSucceeded(e -> {
            List<NativeWindowUtils.WindowInfo> windows = task.getValue();
            if (!windows.isEmpty()) {
                windowPreviewPopup.updateContent(windows, icon, model);
                windowPreviewPopup.showAbove(button);

                // Also handle mouse over popup to prevent hiding
                windowPreviewPopup.getContainer().setOnMouseEntered(ev -> hideTimer.stop());
                windowPreviewPopup.getContainer().setOnMouseExited(ev -> hideTimer.playFromStart());
            }
        });

        task.setOnFailed(e -> {
            Logger.error("Failed to fetch windows for " + item.getLabel() + ": " + task.getException().getMessage());
        });

        new Thread(task).start();
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
