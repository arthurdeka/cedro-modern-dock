package com.github.arthurdeka.cedromoderndock.view;

import com.github.arthurdeka.cedromoderndock.util.NativeWindowUtils;
import com.github.arthurdeka.cedromoderndock.util.NativeWindowUtils.WindowInfo;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

import java.util.List;

public class WindowPreviewPopup extends Popup {

    public WindowPreviewPopup(List<WindowInfo> windows, Image appIcon) {
        VBox content = new VBox();
        content.getStyleClass().add("window-list-popup");
        content.setSpacing(5);
        content.setMinWidth(200);

        // Load CSS
        content.getStylesheets().add(getClass().getResource("/com/github/arthurdeka/cedromoderndock/css/dock.css").toExternalForm());

        for (WindowInfo info : windows) {
            HBox item = new HBox();
            item.getStyleClass().add("window-list-item");
            item.setAlignment(Pos.CENTER_LEFT);
            item.setSpacing(10);
            item.setPrefWidth(200);

            ImageView iconView = new ImageView(appIcon);
            iconView.setFitWidth(24);
            iconView.setFitHeight(24);
            iconView.setPreserveRatio(true);

            Label titleLabel = new Label(info.title());
            titleLabel.getStyleClass().add("window-list-label");
            titleLabel.setMaxWidth(160); // limit width to avoid huge popups
            titleLabel.setWrapText(false);

            item.getChildren().addAll(iconView, titleLabel);

            item.setOnMouseClicked(e -> {
                NativeWindowUtils.activateWindow(info.hwnd());
                this.hide();
            });

            content.getChildren().add(item);
        }

        this.getContent().add(content);
        // Do not auto-hide on focus loss, we manage visibility manually
        this.setAutoHide(false);
    }
}
