package com.example.pipegame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class MainMenu extends Application {

    @Override
    public void start(Stage stage) {
        showWindow("hello-view", stage);
    }

    public static void showWindow(String fxml, Stage stage){
        FXMLLoader fxmlLoader = new FXMLLoader(MainMenu.class.getResource(fxml + ".fxml"));
        Scene scene;
        try {
            Parent root = fxmlLoader.load();
            scene = new Scene(root);
            stage = new Stage();
            stage.setScene(scene);
            stage.sizeToScene();
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static File getFile(String fileName) {
        return new File(Objects.requireNonNull(MainMenu.class.getResource(fileName)).getPath());
    }

    public static void hideWindow(Stage stage){
        stage.hide();
    }

    public static void main(String[] args) {
        launch();
    }
}