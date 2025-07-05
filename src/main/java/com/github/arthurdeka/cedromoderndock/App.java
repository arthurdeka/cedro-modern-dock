package com.github.arthurdeka.cedromoderndock;

import com.github.arthurdeka.cedromoderndock.controller.DockController;
import com.github.arthurdeka.cedromoderndock.util.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

import static com.github.arthurdeka.cedromoderndock.util.UIUtils.setStageIcon;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {

        // invisble primary stage to dont show the dock icon in the taskbar
        primaryStage.initStyle(StageStyle.UTILITY);
        primaryStage.setOpacity(0);
        primaryStage.show();

        // creates a new stage for the dock
        Stage dockStage = new Stage();

        // loading dock interface and controller.
        FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/DockView.fxml"));
        Scene scene = new Scene(loader.load());

        DockController dockController = loader.getController();
        dockController.setStage(dockStage);
        dockController.handleInitialization();

        // configuring dock stage.
        dockStage.setTitle("Cedro Modern Dock");
        setStageIcon(dockStage);
        dockStage.setScene(scene);

        // defining the invisible window as the "owner" of the dock (this makes the dock invisible).
        dockStage.initOwner(primaryStage);
        dockStage.initStyle(StageStyle.TRANSPARENT);

        scene.setFill(Color.TRANSPARENT);
        dockStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}