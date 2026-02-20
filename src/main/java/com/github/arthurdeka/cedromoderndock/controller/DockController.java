package com.github.arthurdeka.cedromoderndock.controller;

import com.github.arthurdeka.cedromoderndock.App;
import com.github.arthurdeka.cedromoderndock.model.*;
import com.github.arthurdeka.cedromoderndock.util.Logger;
import com.github.arthurdeka.cedromoderndock.util.SaveAndLoadDockSettings;
import com.github.arthurdeka.cedromoderndock.util.WindowsIconHandler;
import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.util.Duration;
import com.github.arthurdeka.cedromoderndock.util.NativeWindowUtils;
import com.github.arthurdeka.cedromoderndock.view.WindowPreviewPopup;
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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.github.arthurdeka.cedromoderndock.util.UIUtils.setStageIcon;

public class DockController {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private HBox hBoxContainer;

    private DockModel model;
    private Stage stage;
    // Runs native window queries off the FX thread; single daemon thread avoids unbounded thread creation.
    private final ExecutorService windowPreviewExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "WindowPreviewFetcher");
        t.setDaemon(true);
        return t;
    });
    private WindowPreviewPopup windowPreviewPopup;
    private PauseTransition hideDebounce;
    private boolean isHoveringPopup = false;
    private Button currentHoverButton;
    private Button popupOwnerButton;
    // Monotonic id to ignore stale async results from previous hover requests.
    private int hoverRequestId = 0;

    // variables for the enableDrag function
    private double xOffset = 0;
    private double yOffset = 0;

    // Run when FXML is loaded
    public void handleInitialization() {
        model = SaveAndLoadDockSettings.load();

        // Popup that lists open windows for a program icon on hover.
        windowPreviewPopup = new WindowPreviewPopup();
        windowPreviewPopup.getContainer().setOnMouseEntered(e -> {
            isHoveringPopup = true;
            hideDebounce.stop();
        });
        windowPreviewPopup.getContainer().setOnMouseExited(e -> {
            isHoveringPopup = false;
            scheduleHide();
        });

        // Small delay prevents flicker when moving between icon and popup.
        hideDebounce = new PauseTransition(Duration.millis(80));
        hideDebounce.setOnFinished(e -> {
            if (shouldHidePreview()) {
                windowPreviewPopup.hide();
                popupOwnerButton = null;
            }
        });

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
        } else if (item instanceof DockProgramItemModel) {
            // Logic for DockProgramItemModel runs on a background thread.
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

            // Show a window list preview when hovering this program icon.
            setupHoverPreview(button, (DockProgramItemModel) item, icon);

            return button;

        } else {
            return null;
        }
    }

    private void setupHoverPreview(Button button, DockProgramItemModel item, Image icon) {
        // Track hover state for the icon and popup to avoid flicker.
        button.setOnMouseEntered(e -> {
            currentHoverButton = button;
            hideDebounce.stop();
            // If another icon owns the popup, close it before showing new content.
            if (windowPreviewPopup.isShowing() && popupOwnerButton != button) {
                windowPreviewPopup.hide();
                popupOwnerButton = null;
            }
            showWindowPreview(button, item, icon);
        });

        button.setOnMouseExited(e -> {
            if (currentHoverButton == button) {
                currentHoverButton = null;
            }
            // Hide with a short debounce to allow moving into the popup.
            scheduleHide();
        });
    }

    private void showWindowPreview(Button button, DockProgramItemModel item, Image icon) {
        int requestId = ++hoverRequestId;
        // Query native windows on a background thread.
        Task<List<NativeWindowUtils.WindowInfo>> task = new Task<>() {
            @Override
            protected List<NativeWindowUtils.WindowInfo> call() throws Exception {
                return NativeWindowUtils.getOpenWindows(item.getPath());
            }
        };

        task.setOnSucceeded(e -> {
            // Ignore results from older hover requests.
            if (requestId != hoverRequestId) {
                return;
            }
            List<NativeWindowUtils.WindowInfo> windows = task.getValue();
            // If the mouse left the icon, do nothing.
            if (currentHoverButton != button || !button.isHover()) {
                return;
            }
            // Only show popup when there is at least one window.
            if (!windows.isEmpty()) {
                windowPreviewPopup.updateContent(windows, icon, model);
                windowPreviewPopup.showAbove(button, hBoxContainer);
                popupOwnerButton = button;

            } else if (windowPreviewPopup.isShowing() && popupOwnerButton == button) {
                windowPreviewPopup.hide();
                popupOwnerButton = null;
            }
        });

        task.setOnFailed(e -> {
            Logger.error("Failed to fetch windows for " + item.getLabel() + ": " + task.getException().getMessage());
        });

        windowPreviewExecutor.execute(task);
    }

    private void scheduleHide() {
        hideDebounce.stop();
        hideDebounce.playFromStart();
    }

    private boolean shouldHidePreview() {
        if (isHoveringPopup) {
            return false;
        }
        if (currentHoverButton == null) {
            return true;
        }
        return !currentHoverButton.isHover();
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
