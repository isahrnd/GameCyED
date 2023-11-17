package com.example.pipegame.control;

import com.example.pipegame.model.AdjacencyListGraph;
import com.example.pipegame.model.Pipe;
import com.example.pipegame.model.Vertex;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class Game implements Initializable {

    @FXML
    private Label text;
    @FXML
    private GridPane board;
    private Vertex<Pipe> previousVertex;
    private AdjacencyListGraph<Pipe> graphL;
    private final ArrayList<Pipe> pipes = new ArrayList<>();
    private int currentImageIndex = 1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeGraph();
        board.setOnMouseClicked(this::handleGridClick);
    }

    private void initializeGraph() {
        graphL = new AdjacencyListGraph<>();
        for (int row = 0; row < board.getRowCount(); row++) {
            for (int col = 0; col < board.getColumnCount(); col++) {
                Vertex<Pipe> vertex = new Vertex<>(new Pipe(-1,row, col));
                graphL.addVertex(vertex);
            }
        }
    }

    private void handleGridClick(MouseEvent event) {
        // Obtenemos las coordenadas de la celda en la que se hizo clic
        int columnIndex = (int) (event.getX() / (board.getWidth() / board.getColumnCount()));
        int rowIndex = (int) (event.getY() / (board.getHeight() / board.getRowCount()));

        //Actualizamos tablero

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
        System.out.println("Clic en la columna: " + columnIndex + ", Fila: " + rowIndex);

        //Actualizamos el grafo
        Vertex<Pipe> currentVertex = getVertexFromCell(columnIndex, rowIndex);
        if (previousVertex != null) {
            graphL.addEdge(previousVertex, currentVertex);
        }
        // Actualizar el vértice anterior
        previousVertex = currentVertex;
    }

    private Vertex<Pipe> getVertexFromCell(int columnIndex, int rowIndex) {
        // Iteramos sobre los vértices y encontrar el vértice correspondiente a las coordenadas
        for (Vertex<Pipe> vertex : graphL.getVertices()) {
            if (vertex.getData().getX() == rowIndex && vertex.getData().getY() == columnIndex) {
                return vertex;
            }
        }
        return null;
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

    @FXML
    protected void validateButton() {
        Vertex<Pipe> sourceVertex = getVertexFromCell(1,1);
        Vertex<Pipe> drainVertex = getVertexFromCell(1,5);
        // validación utilizando BFS
        ArrayList<Vertex<Pipe>> path = graphL.bfs(sourceVertex);
        if (path.contains(drainVertex)) {
            System.out.println("Camino valido");
        } else {
            System.out.println("Camino invalido");
        }
    }

    @FXML
    protected void surrenderButton() {
    }

    @FXML
    protected void resetButton() {

    }

    @FXML
    protected void menuButton() {

    }
}
