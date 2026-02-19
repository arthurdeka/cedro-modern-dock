package com.github.arthurdeka.cedromoderndock.view;

import com.github.arthurdeka.cedromoderndock.model.DockModel;
import com.github.arthurdeka.cedromoderndock.util.NativeWindowUtils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Popup;

import java.util.List;

public class WindowPreviewPopup extends Popup {

    private final VBox container;
    private Node currentTarget;
    private final ChangeListener<Number> sizeListener = (obs, old, val) -> reposition();

    public WindowPreviewPopup() {
        this.container = new VBox();
        this.container.setPadding(new Insets(10));
        this.container.setSpacing(5);
        this.container.setAlignment(Pos.CENTER_LEFT);

        // Prevent auto-hide when clicking inside the popup
        this.setAutoHide(false);
        this.getContent().add(container);

        widthProperty().addListener(sizeListener);
        heightProperty().addListener(sizeListener);
    }

    public void updateContent(List<NativeWindowUtils.WindowInfo> windows, Image appIcon, DockModel model) {
        container.getChildren().clear();

        // Apply style from model
        String style = String.format(
            "-fx-background-color: rgba(%s %s); -fx-background-radius: %s;",
            model.getDockColorRGB(),
            model.getDockTransparency(),
            model.getDockBorderRounding()
        );
        container.setStyle(style);

        Color textColor = getTextColorForBackground(model.getDockColorRGB());

        for (NativeWindowUtils.WindowInfo window : windows) {
            HBox item = createWindowItem(window, appIcon, textColor);
            container.getChildren().add(item);
        }
    }

    private Color getTextColorForBackground(String dockColorRGB) {
        try {
            // Remove commas and trim
            String cleanRGB = dockColorRGB.replace(",", " ").trim();
            String[] parts = cleanRGB.split("\\s+");
            if (parts.length >= 3) {
                int r = Integer.parseInt(parts[0]);
                int g = Integer.parseInt(parts[1]);
                int b = Integer.parseInt(parts[2]);
                // Calculate brightness
                double brightness = (r * 0.299 + g * 0.587 + b * 0.114);
                return brightness > 128 ? Color.BLACK : Color.WHITE;
            }
        } catch (NumberFormatException e) {
            // Ignore, default to white
        }
        return Color.WHITE;
    }

    private HBox createWindowItem(NativeWindowUtils.WindowInfo window, Image appIcon, Color textColor) {
        HBox item = new HBox();
        item.setSpacing(10);
        item.setPadding(new Insets(5, 10, 5, 10));
        item.setAlignment(Pos.CENTER_LEFT);
        item.setStyle("-fx-background-color: transparent; -fx-background-radius: 5; -fx-cursor: hand;");

        // Icon
        ImageView iconView = new ImageView(appIcon);
        iconView.setFitWidth(16);
        iconView.setFitHeight(16);
        iconView.setPreserveRatio(true);

        // Title
        Label titleLabel = new Label(window.title());
        titleLabel.setTextFill(textColor);
        titleLabel.setStyle("-fx-font-size: 12px;");

        item.getChildren().addAll(iconView, titleLabel);

        // Hover effect
        item.setOnMouseEntered(e -> {
            item.setStyle("-fx-background-color: rgba(255, 255, 255, 0.2); -fx-background-radius: 5; -fx-cursor: hand;");
            titleLabel.setTextFill(Color.WHITE); // Always white on hover for better contrast against hover bg
        });
        item.setOnMouseExited(e -> {
            item.setStyle("-fx-background-color: transparent; -fx-background-radius: 5; -fx-cursor: hand;");
            titleLabel.setTextFill(textColor);
        });

        // Click action
        item.setOnMouseClicked(e -> {
            NativeWindowUtils.activateWindow(window.hwnd());
            this.hide();
        });

        return item;
    }

    public VBox getContainer() {
        return container;
    }

    public void showAbove(Node target) {
        this.currentTarget = target;
        Bounds bounds = target.localToScreen(target.getBoundsInLocal());

        if (!isShowing()) {
            this.show(target, bounds.getMinX(), bounds.getMinY());
        }
        reposition();
    }

    private void reposition() {
        if (currentTarget == null || !isShowing()) return;
        Bounds bounds = currentTarget.localToScreen(currentTarget.getBoundsInLocal());
        if (bounds == null) return;

        double w = getWidth();
        double h = getHeight();

        setX(bounds.getMinX() + (bounds.getWidth() / 2) - (w / 2));
        setY(bounds.getMinY() - h - 10);
    }
}
