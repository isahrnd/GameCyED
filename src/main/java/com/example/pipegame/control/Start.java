package com.example.pipegame.control;

import com.example.pipegame.MainMenu;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Start {

    @FXML
    private Label vText;

    @FXML
    protected void PlayButton() {
        int mode = askForGraphType();
        if (mode != 0) {
            Game.selectedGraphMode = mode;
            MainMenu.hideWindow((Stage) vText.getScene().getWindow());
            MainMenu.showWindow("game-view", null);
        }
    }

    private int askForGraphType() {
        ArrayList<String> options = new ArrayList<>(List.of("Adjacency List", "Adjacency Matrix"));
        String selectedOption = MainMenu.showChoiceDialog("Confirmation", "Set graph type", "Select an option:", options);
        if ("Adjacency List".equals(selectedOption)) {
            return 1;
        } else if ("Adjacency Matrix".equals(selectedOption)) {
            return 2;
        }
        return 0;
    }
}