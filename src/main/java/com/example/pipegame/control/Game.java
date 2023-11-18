package com.example.pipegame.control;

import com.example.pipegame.MainMenu;
import com.example.pipegame.model.AdjacencyListGraph;
import com.example.pipegame.model.Pipe;
import com.example.pipegame.model.Vertex;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

public class Game implements Initializable {

    @FXML
    private Label text;
    @FXML
    private GridPane board;
    @FXML
    private Canvas canvas;
    private GraphicsContext gc;
    private Vertex<Pipe> previousVertex;
    private Vertex<Pipe> sourceVertex;
    private Vertex<Pipe> drainVertex;
    private AdjacencyListGraph<Pipe> graphL;
    private final ArrayList<Pipe> pipesOnScreen = new ArrayList<>();
    private int currentImageIndex = 1;
    private boolean[][] blockedCells;
    private Image source, drain;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gc = canvas.getGraphicsContext2D();
        canvas.setFocusTraversable(true);
        getSourceAndDrainImage();
        initializeGame();
        board.setOnMouseClicked(this::handleGridClick);
    }

    private void initializeGame() {
        generateBlockedCells();
        printBlockedCells();
        Platform.runLater(() -> {
            initializeGraph();
            addSourceAndDrainVertex();
            buildGraphWithoutPipes();
            if (hasPath()) {
                paintFountainAndDraw();
                graphL.removeAllEdges();
            } else {
                MainMenu.informationWindow("Sorry, the game created has no solution, please try again.");
                MainMenu.hideWindow((Stage)text.getScene().getWindow());
                MainMenu.showWindow("hello-view", null);
            }
        });
    }


    private boolean hasPath() {
        ArrayList<Vertex<Pipe>> path = graphL.bfs(sourceVertex);
        return path.contains(drainVertex);
    }

    private void printBlockedCells() {
        for (boolean[] row : blockedCells) {
            for (boolean cell : row) {
                System.out.print(cell ? "X " : "O ");
            }
            System.out.println();
        }
    }

    private void generateBlockedCells(){
        blockedCells = new boolean[10][10];
        Random random = new Random();
        int blockedCount = 0;
        while (blockedCount < 30) {
            int row = random.nextInt(board.getRowCount());
            int col = random.nextInt(board.getColumnCount());
            if (!isCellBlocked(row, col)) {
                blockedCells[row][col] = true;
                blockedCount++;
            }
        }
    }

    private void initializeGraph() {
        graphL = new AdjacencyListGraph<>();
        for (int row = 0; row < board.getRowCount(); row++) {
            for (int col = 0; col < board.getColumnCount(); col++) {
                boolean isBlocked = blockedCells[row][col];
                if (!isBlocked) {
                    Vertex<Pipe> vertex = new Vertex<>(new Pipe(-1, row, col));
                    graphL.addVertex(vertex);
                } else {
                    Rectangle rectangle = new Rectangle(board.getWidth() / board.getColumnCount(), board.getHeight() / board.getRowCount());
                    rectangle.setFill(Color.BLACK);
                    board.add(rectangle, col, row);
                }
            }
        }
    }

    private void addSourceAndDrainVertex(){
        int[] cols = generateSourceAndDrainCols();
        sourceVertex = getVertexFromCell(cols[0],0);
        drainVertex = getVertexFromCell(cols[1],9);
        System.out.println("source: "+sourceVertex.getData().getRow()+" "+sourceVertex.getData().getCol());
        System.out.println("fountain: "+drainVertex.getData().getRow()+" "+drainVertex.getData().getCol());
    }

    public void paintFountainAndDraw() {
        gc.drawImage(source, allowedXCoordinates(sourceVertex.getData().getCol()), 0, 37, 23);
        gc.drawImage(drain, allowedXCoordinates(drainVertex.getData().getCol()), 376, 37, 24);
    }

    private int allowedXCoordinates(int position){
        int[] x_coordinates = {46,82,117,152,188,223,258,293,329,364};
        return x_coordinates[position];
    }

    private int[] generateSourceAndDrainCols() {
        Random random = new Random();
        int fountain_col = random.nextInt(board.getColumnCount());
        while (isCellBlocked(0,fountain_col)){
            fountain_col = random.nextInt(board.getColumnCount());
        }
        int drain_col = random.nextInt(board.getColumnCount());
        while (isCellBlocked(9,drain_col)){
            drain_col = random.nextInt(board.getColumnCount());
        }
        return new int[]{fountain_col, drain_col};
    }

    private boolean isCellBlocked(int row, int col) {
        return blockedCells[row][col];
    }


    private void handleGridClick(MouseEvent event) {
        // Obtenemos las coordenadas de la celda en la que se hizo clic
        int columnIndex = (int) (event.getX() / (board.getWidth() / board.getColumnCount()));
        int rowIndex = (int) (event.getY() / (board.getHeight() / board.getRowCount()));

        if (isCellBlocked(rowIndex, columnIndex)) {
            System.out.println("No puedes colocar tuberías en una celda bloqueada.");
            return;
        }

        //Actualizamos tablero

        // Verificamos si ya hay un objeto en la casilla
        Pipe existingObject = getObjectInCell(columnIndex, rowIndex);
        if (existingObject != null) {
            pipesOnScreen.remove(existingObject);
            board.getChildren().removeIf(node -> GridPane.getColumnIndex(node) != null &&
                    GridPane.getRowIndex(node) != null &&
                    GridPane.getColumnIndex(node) == columnIndex &&
                    GridPane.getRowIndex(node) == rowIndex
            );
        }
        // Creamos una tubería con la imagen actual y las coordenadas
        Pipe customObject = new Pipe(currentImageIndex, columnIndex, rowIndex);
        // Agregamos a la lista
        pipesOnScreen.add(customObject);
        showImageInBoard(customObject.getImage(),columnIndex,rowIndex);
        // Incrementamos el índice para la siguiente imagen (ciclo circular)
        currentImageIndex = (currentImageIndex % 3) + 1;
        System.out.println(pipesOnScreen.size());
        System.out.println("Clic en la columna: " + columnIndex + ", Fila: " + rowIndex);

        //Actualizamos el grafo
        Vertex<Pipe> currentVertex = getVertexFromCell(columnIndex, rowIndex);
        if (previousVertex != null) {
            graphL.addEdge(previousVertex, currentVertex,1);
            System.out.println("creo arista");
        }
        // Actualizar el vértice anterior
        previousVertex = currentVertex;
    }

    private Vertex<Pipe> getVertexFromCell(int columnIndex, int rowIndex) {
        for (Vertex<Pipe> vertex : graphL.getVertices()) {
            if (vertex.getData().getRow() == rowIndex && vertex.getData().getCol() == columnIndex) {
                return vertex;
            }
        }
        return null;
    }

    private Pipe getObjectInCell(int columnIndex, int rowIndex) {
        for (Pipe obj : pipesOnScreen) {
            if (obj.getCol() == columnIndex && obj.getRow() == rowIndex) {
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
        // Validación utilizando DFS
        ArrayList<Vertex<Pipe>> path = graphL.dfs(sourceVertex);
        System.out.println(path.size());
        if (path.contains(drainVertex)) {
            System.out.println("Camino válido");
        } else {
            System.out.println("Camino inválido");
        }
    }


    @FXML
    protected void surrenderButton() {
        graphL.removeAllEdges();
        buildGraphWithoutPipes();
        ArrayList<Vertex<Pipe>> shortestPath = graphL.dijkstra(sourceVertex, drainVertex);
        highlightShortestPath(shortestPath);
    }

    private void buildGraphWithoutPipes() {
        System.out.println("Free celds:");
        for (int row = 0; row < board.getRowCount(); row++) {
            for (int col = 0; col < board.getColumnCount(); col++) {
                Vertex<Pipe> currentVertex = getVertexFromCell(row, col);
                if (currentVertex != null){
                    int x = currentVertex.getData().getRow();
                    int y = currentVertex.getData().getCol();
                    System.out.println(x+" "+y);
                    connectWithNeighbors(currentVertex);
                }
            }
        }
    }

    private void connectWithNeighbors(Vertex<Pipe> vertex) {
        int row = vertex.getData().getRow();
        int col = vertex.getData().getCol();
        // Conectar con el vecino superior (si existe)
        if (row > 0) {
            Vertex<Pipe> neighbor = getVertexFromCell(col, row-1);
            if (neighbor != null){
                graphL.addEdge(vertex, neighbor,1);
                System.out.println("vertice "+row+", "+col+" conectado arriba");
            }
        }

        // Conectar con el vecino inferior (si existe)
        if (row < board.getRowCount() - 1) {
            Vertex<Pipe> neighbor = getVertexFromCell(col, row+1);
            if (neighbor != null){
                graphL.addEdge(vertex, neighbor,1);
                System.out.println("vertice "+row+", "+col+" conectado abajo");
            }

        }

        // Conectar con el vecino izquierdo (si existe)
        if (col > 0) {
            Vertex<Pipe> neighbor = getVertexFromCell(col-1, row);
            if (neighbor != null){
                graphL.addEdge(vertex, neighbor,1);
                System.out.println("vertice "+row+", "+col+" conectado a la izquierda");
            }
        }

        // Conectar con el vecino derecho (si existe)
        if (col < board.getColumnCount() - 1) {
            Vertex<Pipe> neighbor = getVertexFromCell(col+1, row);
            if (neighbor != null){
                graphL.addEdge(vertex, neighbor,1);
                System.out.println("vertice "+row+", "+col+" conectado a la derecha");
            }
        }
    }

    private void highlightShortestPath(ArrayList<Vertex<Pipe>> shortestPath) {
        for (Vertex<Pipe> vertex : shortestPath) {
            int columnIndex = vertex.getData().getCol();
            int rowIndex = vertex.getData().getRow();
            Rectangle rectangle = new Rectangle(board.getWidth() / board.getColumnCount(), board.getHeight() / board.getRowCount());
            rectangle.setFill(Color.YELLOW); // Establecer el color deseado
            // Agregar el Rectangle al GridPane en la posición específica
           board.add(rectangle, columnIndex, rowIndex);
        }
    }


    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }

    @FXML
    protected void resetButton() {
        Rectangle rectangle = new Rectangle(board.getWidth() / board.getColumnCount(), board.getHeight() / board.getRowCount());
        rectangle.setFill(Color.BLACK);
        board.add(rectangle, 0, 0);
    }

    @FXML
    protected void menuButton() {
        MainMenu.hideWindow((Stage)text.getScene().getWindow());
        MainMenu.showWindow("hello-view", null);
    }

    private void getSourceAndDrainImage(){
        Image image = new Image("file:" + MainMenu.getFile("images/pipe_2.png").getPath());
        source = image;
        drain = image;
    }
}
