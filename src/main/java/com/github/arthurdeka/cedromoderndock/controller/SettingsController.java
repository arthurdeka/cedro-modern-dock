package com.github.arthurdeka.cedromoderndock.controller;

import com.github.arthurdeka.cedromoderndock.model.DockItem;
import com.github.arthurdeka.cedromoderndock.model.DockProgramItemModel;
import com.github.arthurdeka.cedromoderndock.model.DockSettingsItemModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class SettingsController {

    @FXML
    private ListView listView;

    @FXML
    private Button addProgramButton;
    @FXML
    private Button removeProgramButton;
    @FXML
    private Button moveItemUpButton;
    @FXML
    private Button moveItemDownButton;

    private DockController dockController;

    private ObservableList<String> listItems = FXCollections.observableArrayList();

    // Run when FXML is loaded
    public void initialize() {
        System.out.println("[Initializing] SettingsController");

        // add listener to listView
        listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
                handleListViewItemSelection();
            }
        });

    }

    private void handleListViewItemSelection() {
        int selectedIdx = listView.getSelectionModel().getSelectedIndex();
        DockItem item = dockController.getDockItems().get(selectedIdx);

        // disables removeProgramButton if the selected item is the Settings item
        if (item instanceof DockSettingsItemModel) {
            removeProgramButton.setDisable(true);
        } else {
            removeProgramButton.setDisable(false);

        }

        // disables moveItemUpButton if item is already at top or bottom of the lsit.
        if (selectedIdx == 0) {
            moveItemUpButton.setDisable(true);
        } else {
            moveItemUpButton.setDisable(false);
        }

        // disables moveItemDownButton if item is already at top or bottom of the lsit.
        if (selectedIdx == listItems.size() - 1) {
            moveItemDownButton.setDisable(true);

        } else {
            moveItemDownButton.setDisable(false);
        }


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

        // deletes selected option
        System.out.println("[listView] Removing item on index: " + selectedIdx);

        dockController.removeDockItem(selectedIdx);
        listItems.remove(selectedIdx);

        dockController.refreshUI();

    }

    @FXML
    private void HandleMoveItem(ActionEvent event) {
        int selectedIdx = listView.getSelectionModel().getSelectedIndex();

        if (event.getSource() == moveItemUpButton) {
            System.out.println("[listView] moving item up");
            Collections.swap(listItems, selectedIdx, selectedIdx - 1);
            dockController.swapItems(selectedIdx, selectedIdx - 1);

            // set new position as selected
            listView.getSelectionModel().select(selectedIdx - 1);

        } else {
            System.out.println("[listView] moving item down");
            Collections.swap(listItems, selectedIdx, selectedIdx + 1);
            dockController.swapItems(selectedIdx, selectedIdx + 1);

            // set new position as selected
            listView.getSelectionModel().select(selectedIdx + 1);

        }


    }

    public void setDockController(DockController dockController) {
        this.dockController = dockController;
    }

}


