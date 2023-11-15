package com.example.pipegame.control;

import com.example.pipegame.model.Pipe;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Game implements Initializable {

    @FXML
    private GridPane board;
    private final ArrayList<Pipe> pipes = new ArrayList<>();
    private int currentImageIndex = 1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        board.setOnMouseClicked(this::handleGridClick);
    }

    private void handleGridClick(MouseEvent event) {
        // Obtenemos las coordenadas de la celda en la que se hizo clic
        int columnIndex = (int) (event.getX() / (board.getWidth() / board.getColumnCount()));
        int rowIndex = (int) (event.getY() / (board.getHeight() / board.getRowCount()));
        // Verificamos si ya hay un objeto en la casilla
        Pipe existingObject = getObjectInCell(columnIndex, rowIndex);
        if (existingObject != null) {
            pipes.remove(existingObject);
            board.getChildren().removeIf(node -> GridPane.getColumnIndex(node) != null &&
                    GridPane.getRowIndex(node) != null &&
                    GridPane.getColumnIndex(node) == columnIndex &&
                    GridPane.getRowIndex(node) == rowIndex
            );
        }
        // Creamos una tubería con la imagen actual y las coordenadas
        Pipe customObject = new Pipe(currentImageIndex, columnIndex, rowIndex);
        // Agregamos a la lista
        pipes.add(customObject);
        showImageInBoard(customObject.getImage(),columnIndex,rowIndex);
        // Incrementamos el índice para la siguiente imagen (ciclo circular)
        currentImageIndex = (currentImageIndex % 3) + 1;
        System.out.println(pipes.size());
    }

    private Pipe getObjectInCell(int columnIndex, int rowIndex) {
        for (Pipe obj : pipes) {
            if (obj.getX() == columnIndex && obj.getY() == rowIndex) {
                return obj;
            }
        }
        return null;
    }

    private void showImageInBoard(ImageView image, int columnIndex, int rowIndex){
        image.setFitWidth(board.getWidth() / board.getColumnCount());
        image.setFitHeight(board.getHeight() / board.getRowCount());
        board.add(image, columnIndex, rowIndex);
    }
}
