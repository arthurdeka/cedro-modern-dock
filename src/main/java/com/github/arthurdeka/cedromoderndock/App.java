package com.github.arthurdeka.cedromoderndock;

import com.github.arthurdeka.cedromoderndock.controller.DockController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        System.out.println("Olá!");
        FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/dock.fxml"));
        Scene scene = new Scene(loader.load());

        DockController dockController = loader.getController();
        dockController.setStage(stage);

        stage.setTitle("Cedro Modern Dock");
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}