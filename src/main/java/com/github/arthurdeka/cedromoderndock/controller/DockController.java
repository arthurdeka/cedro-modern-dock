package com.github.arthurdeka.cedromoderndock.controller;

import com.github.arthurdeka.cedromoderndock.App;
import com.github.arthurdeka.cedromoderndock.model.*;
import com.github.arthurdeka.cedromoderndock.util.WindowsIconExtractor;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.List;


public class DockController {

    @FXML
    private HBox hBoxContainer;

    private DockModel model;
    private Stage stage;

    // Run when FXML is loaded
    public void initialize() {
        model = new DockModel();

        model.loadDefaultItems();
        updateDockUI();

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

    private void updateDockUI() {
        hBoxContainer.getChildren().clear();


        for(DockItem item : model.getItems()) {
            Button button = createButton(item);
            hBoxContainer.getChildren().add(button);
        }

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


        } else if (item instanceof DockProgramItemModel) {
            String exePath = item.getPath();

            ImageView imageView = null;


            try {
                Image icon = WindowsIconExtractor.getExeIcon(exePath);
                imageView = new ImageView((icon));
            } catch (Exception e) {
                System.out.println("ERROR - It was not possible to load the program's icon");
            }

            imageView.setFitWidth(model.getIconsSize());
            imageView.setFitHeight(model.getIconsSize());

            Button button = new Button(item.getLabel());
            button.getStyleClass().add("dock-button");
            button.setGraphic(imageView);

            button.setOnAction(e -> item.performAction());


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
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void setDockIconsSize(int iconsSize) {
        model.setIconsSize(iconsSize);
        refreshUI();
    }

    public int getDockIconsSize() {
        return model.getIconsSize();
    }

    public void refreshUI() {
        updateDockUI();
        // resize window to account for DockItem additions or removings
        stage.sizeToScene();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

}

