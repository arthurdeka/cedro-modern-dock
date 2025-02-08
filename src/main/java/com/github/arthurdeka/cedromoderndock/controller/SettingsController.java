package com.github.arthurdeka.cedromoderndock.controller;

import com.github.arthurdeka.cedromoderndock.model.DockItem;
import com.github.arthurdeka.cedromoderndock.model.DockModel;
import com.github.arthurdeka.cedromoderndock.model.DockProgramItemModel;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Paths;

public class SettingsController {

    @FXML
    private Button addProgramButton;

    private DockModel dockModel;
    private DockController dockController;

    // Run when FXML is loaded
    public void initialize() {
        System.out.println("oi");
    }

    public void setDockModel(DockModel dockModel) {
        this.dockModel = dockModel;


    }

    @FXML
    private void handleAddProgram() {
        // opens a file chooser and creates a new DockItem using file path and file icon info
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose .exe");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Executable", "*.exe"));

        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            // obs: the .exe file path will be used to extract the icon path as well.
            String selectedExePath = file.getAbsolutePath();
            String selectedExeName = Paths.get(file.getAbsolutePath()).getFileName().toString().replace(".exe", "");

            DockItem newItem = new DockProgramItemModel(selectedExeName, selectedExePath);
            dockModel.addItem(newItem);
            System.out.println("Program added: " + selectedExeName);

            //closes window
            Stage stage = (Stage) addProgramButton.getScene().getWindow();
            stage.close();
        }

        dockController.refreshUI();

    }


    public void setDockController(DockController dockController) {
        this.dockController = dockController;
    }
}


