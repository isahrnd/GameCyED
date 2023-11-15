package com.example.pipegame.control;

import com.example.pipegame.MainMenu;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class Start {

    @FXML
    private Label text;

    @FXML
    protected void PlayButton() {
        MainMenu.hideWindow((Stage)text.getScene().getWindow());
        MainMenu.showWindow("game-view", null);
    }
}