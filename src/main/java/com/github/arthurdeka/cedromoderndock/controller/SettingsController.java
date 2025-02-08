package com.github.arthurdeka.cedromoderndock.controller;

import com.github.arthurdeka.cedromoderndock.model.DockItem;
import com.github.arthurdeka.cedromoderndock.model.DockModel;
import com.github.arthurdeka.cedromoderndock.model.DockProgramItemModel;
import com.github.arthurdeka.cedromoderndock.model.DockSettingsItemModel;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SettingsController {

    @FXML
    private ListView listView;

    @FXML
    private Button addProgramButton;
    @FXML
    private Button removeProgramButton;

    private DockController dockController;

    private ObservableList<String> listItems = FXCollections.observableArrayList();

    // Run when FXML is loaded
    public void initialize() {
        System.out.println("[Initializing] SettingsController");
    }


    public void addDockItemsToListView(List<DockItem> DockItems) {

        for (DockItem item : DockItems) {
            listItems.add(item.getLabel());
            System.out.println("[Initializing][listView] Adding item to ListView: " + item.getLabel());
        }

        // list view will always follow ObservableList<String> listItems
        listView.setItems(listItems);

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
            dockController.addDockItem(newItem);
            System.out.println("[listView] Program added: " + selectedExeName);

            //closes window
            Stage stage = (Stage) addProgramButton.getScene().getWindow();
            stage.close();
        }

        dockController.refreshUI();

    }

    @FXML
    private void handleRemoveProgram() {
        int selectedIdx = listView.getSelectionModel().getSelectedIndex();

        // blocks from deleting the settings option
        DockItem item = dockController.getDockItems().get(selectedIdx);
        if (item instanceof DockSettingsItemModel) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("You can't delete the settings option");
            alert.showAndWait();

        } else {
            // deletes selected option
            System.out.println("[listView] Removing item on index: " + selectedIdx);

            dockController.removeDockItem(selectedIdx);
            listItems.remove(selectedIdx);

            dockController.refreshUI();

        }

    }


    public void setDockController(DockController dockController) {
        this.dockController = dockController;
    }

}


