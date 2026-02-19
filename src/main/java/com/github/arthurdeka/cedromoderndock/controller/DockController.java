package com.github.arthurdeka.cedromoderndock.controller;

import com.github.arthurdeka.cedromoderndock.App;
import com.github.arthurdeka.cedromoderndock.model.*;
import com.github.arthurdeka.cedromoderndock.util.Logger;
import com.github.arthurdeka.cedromoderndock.util.NativeWindowUtils;
import com.github.arthurdeka.cedromoderndock.util.NativeWindowUtils.WindowInfo;
import com.github.arthurdeka.cedromoderndock.util.SaveAndLoadDockSettings;
import com.github.arthurdeka.cedromoderndock.util.WindowsIconHandler;
import com.github.arthurdeka.cedromoderndock.view.WindowPreviewPopup;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
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
import javafx.util.Duration;

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

            // Hover Logic for Window Previews
            PauseTransition hoverDelay = new PauseTransition(Duration.millis(400));
            PauseTransition hideDelay = new PauseTransition(Duration.millis(200));
            final WindowPreviewPopup[] activePopup = {null};
            final boolean[] isHovering = {false};

            button.setOnMouseEntered(e -> {
                isHovering[0] = true;
                hideDelay.stop();
                hoverDelay.setOnFinished(event -> {
                    new Thread(() -> {
                        List<WindowInfo> windows = NativeWindowUtils.getAppWindows(item.getPath());
                        if (!windows.isEmpty()) {
                            Platform.runLater(() -> {
                                if (!isHovering[0]) return; // Check if still hovering
                                if (activePopup[0] != null) activePopup[0].hide();
                                activePopup[0] = new WindowPreviewPopup(windows, imageView.getImage());

                                // Keep popup open if mouse enters it
                                activePopup[0].getContent().get(0).setOnMouseEntered(me -> {
                                    isHovering[0] = true; // Consider hovering popup as hovering context
                                    hideDelay.stop();
                                });
                                activePopup[0].getContent().get(0).setOnMouseExited(me -> {
                                    isHovering[0] = false;
                                    hideDelay.setOnFinished(ev -> {
                                        if (!isHovering[0] && activePopup[0] != null) {
                                            activePopup[0].hide();
                                            activePopup[0] = null;
                                        }
                                    });
                                    hideDelay.playFromStart();
                                });

                                double x = button.localToScreen(button.getBoundsInLocal()).getMinX();
                                double y = button.localToScreen(button.getBoundsInLocal()).getMinY();

                                // Show slightly above the button (assuming dock is bottom)
                                activePopup[0].show(button, x, y);
                                double height = activePopup[0].getContent().get(0).getBoundsInLocal().getHeight();
                                activePopup[0].setY(y - height - 15);
                            });
                        }
                    }).start();
                });
                hoverDelay.playFromStart();
            });

            button.setOnMouseExited(e -> {
                // Only mark not hovering if we haven't moved to the popup
                // But JavaFX events are tricky. We assume we left. The hide delay handles the grace period.
                isHovering[0] = false;
                hoverDelay.stop();
                hideDelay.setOnFinished(event -> {
                    // Check again inside the delay if we re-entered either the button or the popup
                    if (!isHovering[0] && activePopup[0] != null) {
                        activePopup[0].hide();
                        activePopup[0] = null;
                    }
                });
                hideDelay.playFromStart();
            });

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
