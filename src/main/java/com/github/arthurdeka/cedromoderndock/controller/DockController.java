package com.github.arthurdeka.cedromoderndock.controller;

import com.github.arthurdeka.cedromoderndock.App;
import com.github.arthurdeka.cedromoderndock.model.DockItem;
import com.github.arthurdeka.cedromoderndock.model.DockModel;
import com.github.arthurdeka.cedromoderndock.model.DockSettingsItemModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

public class DockController {

    @FXML
    private HBox hBoxContainer;

    private DockModel model;

    // Run when FXML is loaded
    public void initialize() {
        model = new DockModel();
        model.loadDefaultItems();

        updateDockUI();
    }

    private void updateDockUI() {
        for(DockItem item : model.getItems()) {
            Button button = createButton(item);
            hBoxContainer.getChildren().add(button);
        }
    }

    private Button createButton(DockItem item) {

        Image icon = new Image(getClass().getResourceAsStream(item.getIconPath()));
        ImageView imageView = new ImageView(icon);

        imageView.setFitWidth(24);
        imageView.setFitHeight(24);

        Button button = new Button(item.getLabel());
        button.setGraphic(imageView);

        // if the button is the settings button, a different setOnAction is defined
        button.setOnAction(e -> {
            if (item instanceof DockSettingsItemModel) {
                openSettingsWindow(); //
            } else {
                item.performAction(); //
            }
        });

        return button;
    }

    private void openSettingsWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/dock_settings.fxml"));
            Parent root = loader.load();

            SettingsController settingsController = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Settings Window");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}

