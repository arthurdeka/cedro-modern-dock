package com.github.arthurdeka.cedromoderndock.controller;

import com.github.arthurdeka.cedromoderndock.App;
import com.github.arthurdeka.cedromoderndock.model.DockWindowsModuleItemModel;
import com.github.arthurdeka.cedromoderndock.util.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class AddWindowsModulesModalController {

    @FXML
    private ListView listView;

    private DockController dockController;


    // Run when FXML is loaded
    public void initialize() {
        Logger.info("[Initializing] AddWindowsModulesModalController");
        addDefaultItemsToListView();

    }

    private void addDefaultItemsToListView() {
        listView.getItems().add("My Computer");
        listView.getItems().add("Recycle Bin");
        listView.getItems().add("Control Panel");
        listView.getItems().add("Settings");
    }

    @FXML
    private void handleAddSelectedModule() {
        int selectedIdx = listView.getSelectionModel().getSelectedIndex();

        if (selectedIdx == 0) {
            dockController.addDockItem(new DockWindowsModuleItemModel("My Computer", "mypc"));
            dockController.updateDockUI();
        }

        else if (selectedIdx == 1) {
            dockController.addDockItem(new DockWindowsModuleItemModel("Recycle Bin", "trash"));
            dockController.updateDockUI();
        }

        else if (selectedIdx == 2) {
            dockController.addDockItem(new DockWindowsModuleItemModel("Control Panel", "ctrlpnl"));
            dockController.updateDockUI();
        }

        else if (selectedIdx == 3) {
            dockController.addDockItem(new DockWindowsModuleItemModel("Settings", "pconfig"));
            dockController.updateDockUI();
        }

        // come back to dockSettings
        openSettingsWindow();

    }

    private void openSettingsWindow() {
        try {
            // Get reference to the current window/stage
            Stage currentStage = (Stage) listView.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/DockSettingsView.fxml"));
            Parent root = loader.load();

            SettingsController settingsController = loader.getController();
            settingsController.setDockController(dockController);
            settingsController.handleInitialization();

            Stage stage = new Stage();
            stage.setTitle("Settings Window");
            stage.setScene(new Scene(root));
            stage.show();

            // Close the current window
            currentStage.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void setDockController(DockController dockController) {
        this.dockController = dockController;
    }



}
