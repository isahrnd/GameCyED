package com.example.pipegame.control;

import com.example.pipegame.MainMenu;
import com.example.pipegame.model.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
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
                MainMenu.showAlert(Alert.AlertType.INFORMATION,"Information","Game without solution","Sorry, the game created has no solution, please try again.");
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
        System.out.println("free spaces:");
        graphL = new AdjacencyListGraph<>();
        for (int row = 0; row < board.getRowCount(); row++) {
            for (int col = 0; col < board.getColumnCount(); col++) {
                boolean isBlocked = blockedCells[row][col];
                if (!isBlocked) {
                    Vertex<Pipe> vertex = new Vertex<>(new Pipe(-1, row, col));
                    graphL.addVertex(vertex);
                    System.out.println(row+" "+col);
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

        } else {
            currentImageIndex = 1;
        }

        // Creamos una tubería con la imagen actual y las coordenadas
        Pipe customObject = new Pipe(currentImageIndex, rowIndex, columnIndex);
        // Agregamos a la lista
        pipesOnScreen.add(customObject);

        showImageInBoard(customObject.getImage(),columnIndex,rowIndex);
        // Incrementamos el índice para la siguiente imagen (ciclo circular)
        currentImageIndex = (currentImageIndex % 6) + 1;
        System.out.println(pipesOnScreen.size());
        System.out.println("Clic en la columna: " + columnIndex + ", Fila: " + rowIndex);

        //Actualizamos el grafo
        Vertex<Pipe> currentVertex = getVertexFromCell(columnIndex, rowIndex);
        if (currentVertex != null) {
            currentVertex.setData(customObject);
        }
        //if (previousVertex != null && areAdjacent(previousVertex, currentVertex)) {
        //    graphL.addEdge(previousVertex, currentVertex,1);
        //    System.out.println("creo arista");
        //}
        // Actualizar el vértice anterior
        //previousVertex = currentVertex;
    }

    private void connectVerticesWithPipes() {
        for (Pipe pipe : pipesOnScreen){
            Vertex<Pipe> currentVertex = getVertexFromCell(pipe.getCol(), pipe.getRow());
            connectWithNeighbors2(currentVertex);
        }
    }

    private void connectWithNeighbors2(Vertex<Pipe> vertex) {
        boolean isVertexWithPipe = vertex.getData().getType() != null;
        int row = vertex.getData().getRow();
        int col = vertex.getData().getCol();
        // Conectar con el vecino superior (si existe)
        if (row > 0) {
            if (isVertexWithPipe){
                Pipe upPipe = getObjectInCell(col, row - 1);
                if (upPipe != null) {
                    Vertex<Pipe> upVertex = getVertexFromCell(col, row - 1);
                    graphL.addEdge(vertex, upVertex, 1);
                }
            } else {
                Vertex<Pipe> neighbor = getVertexFromCell(col, row-1);
                if (neighbor != null){
                    graphL.addEdge(vertex, neighbor,1);
                    System.out.println("vertice "+row+", "+col+" conectado arriba");
                }
            }
        }

        // Conectar con el vecino inferior (si existe)
        if (row < board.getRowCount() - 1) {
            if (isVertexWithPipe){
                Pipe downPipe = getObjectInCell(col, row + 1);
                if (downPipe != null) {
                    Vertex<Pipe> downVertex = getVertexFromCell(col, row + 1);
                    graphL.addEdge(vertex, downVertex, 1);
                }
            } else {
                Vertex<Pipe> neighbor = getVertexFromCell(col, row+1);
                if (neighbor != null){
                    graphL.addEdge(vertex, neighbor,1);
                    System.out.println("vertice "+row+", "+col+" conectado abajo");
                }
            }

        }

        // Conectar con el vecino izquierdo (si existe)
        if (col > 0) {
            if (isVertexWithPipe){
                Pipe leftPipe = getObjectInCell(col - 1, row);
                if (leftPipe != null) {
                    Vertex<Pipe> leftVertex = getVertexFromCell(col - 1, row);
                    graphL.addEdge(vertex, leftVertex, 1);
                }
            } else {
                Vertex<Pipe> neighbor = getVertexFromCell(col-1, row);
                if (neighbor != null){
                    graphL.addEdge(vertex, neighbor,1);
                    System.out.println("vertice "+row+", "+col+" conectado a la izquierda");
                }
            }
        }

        // Conectar con el vecino derecho (si existe)
        if (col < board.getColumnCount() - 1) {
            if (isVertexWithPipe){
                Pipe downPipe = getObjectInCell(col, row + 1);
                if (downPipe != null) {
                    Vertex<Pipe> downVertex = getVertexFromCell(col, row + 1);
                    graphL.addEdge(vertex, downVertex, 1);
                }
            } else {
                Vertex<Pipe> neighbor = getVertexFromCell(col+1, row);
                if (neighbor != null){
                    graphL.addEdge(vertex, neighbor,1);
                    System.out.println("vertice "+row+", "+col+" conectado a la derecha");
                }
            }
        }
    }


    private boolean areAdjacent(Vertex<Pipe> vertex1, Vertex<Pipe> vertex2) {
        // Verifica si dos vértices son adyacentes (en este caso, comparten una arista)
        int row1 = vertex1.getData().getRow();
        int col1 = vertex1.getData().getCol();
        int row2 = vertex2.getData().getRow();
        int col2 = vertex2.getData().getCol();

        // Dos casillas son adyacentes si comparten una arista en el tablero
        return Math.abs(row1 - row2) + Math.abs(col1 - col2) == 1;
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
        if (validateSourceAndDrain()){
            connectVerticesWithPipes();
            ArrayList<Vertex<Pipe>> path = graphL.bfs(sourceVertex);
            System.out.println(path.size());
            if (path.contains(drainVertex)) {
                if (validatePipeConnections(path)){
                    System.out.println("camino con conexiones valido");
                } else {
                    System.out.println("camino conectado pero con conexiones invalidas");
                }
            } else {
                System.out.println("Camino no conectado");
            }
        } else {
            System.out.println("invalido por fuente o drenaje");
        }
    }

    private boolean validateSourceAndDrain(){
        PipeType sourceType = sourceVertex.getData().getType();
        if (sourceType == PipeType.ELBOW_UP_LEFT || sourceType == PipeType.ELBOW_UP_RIGHT || sourceType == PipeType.VERTICAL){
            PipeType drainType = drainVertex.getData().getType();
            return drainType == PipeType.ELBOW_DOWN_LEFT || drainType == PipeType.ELBOW_DOWN_RIGHT || drainType == PipeType.VERTICAL;
        }
        return false;
    }

    private boolean validatePipeConnections(ArrayList<Vertex<Pipe>> path) {
        for (int i = 0; i < path.size() - 1; i++) {
            Vertex<Pipe> currentVertex = path.get(i);
            Vertex<Pipe> nextVertex = path.get(i + 1);
            Direction direction = getPipeDirection(currentVertex,nextVertex);
            if (!isValidPipeConnection(currentVertex.getData(), nextVertex.getData(), direction)) {
                System.out.println("invalid pipe: "+currentVertex.getData().getRow()+" "+currentVertex.getData().getCol());
            }
        }
        return true;
    }



    private Direction getPipeDirection(Vertex<Pipe> currentVertex, Vertex<Pipe> nextVertex) {
        int currentRow = currentVertex.getData().getRow();
        int currentCol = currentVertex.getData().getCol();
        int nextRow = nextVertex.getData().getRow();
        int nextCol = nextVertex.getData().getCol();
        // Comparar las coordenadas para determinar la dirección
        if (currentRow < nextRow) {
            return Direction.DOWN;
        } else if (currentRow > nextRow) {
            return Direction.UP;
        } else if (currentCol < nextCol) {
            return Direction.RIGHT;
        } else if (currentCol > nextCol) {
            return Direction.LEFT;
        }
        return null;
    }

    private boolean isValidPipeConnection(Pipe currentPipe, Pipe nextPipe, Direction direction) {
        PipeType currentType = currentPipe.getType();
        PipeType nextType = nextPipe.getType();
        if (currentType == PipeType.VERTICAL){
            if (direction == Direction.DOWN){
                return (nextType == PipeType.VERTICAL || nextType == PipeType.ELBOW_UP_LEFT || nextType == PipeType.ELBOW_UP_RIGHT);
            } else if (direction == Direction.UP){
                return (nextType == PipeType.VERTICAL || nextType == PipeType.ELBOW_DOWN_LEFT || nextType == PipeType.ELBOW_DOWN_RIGHT);
            }
        } else if (currentType == PipeType.HORIZONTAL){
            if (direction == Direction.LEFT){
                return (nextType == PipeType.HORIZONTAL || nextType == PipeType.ELBOW_UP_RIGHT || nextType == PipeType.ELBOW_DOWN_RIGHT);
            } else if (direction == Direction.RIGHT){
                return (nextType == PipeType.HORIZONTAL || nextType == PipeType.ELBOW_UP_LEFT || nextType == PipeType.ELBOW_DOWN_LEFT);
            }
        } else if (currentType == PipeType.ELBOW_UP_RIGHT){
            if (direction == Direction.RIGHT){
                return (nextType == PipeType.HORIZONTAL || nextType == PipeType.ELBOW_UP_LEFT || nextType == PipeType.ELBOW_DOWN_LEFT);
            } else if (direction == Direction.UP){
                return (nextType == PipeType.VERTICAL || nextType == PipeType.ELBOW_DOWN_LEFT || nextType == PipeType.ELBOW_DOWN_RIGHT);
            }
        } else if (currentType == PipeType.ELBOW_UP_LEFT){
            if (direction == Direction.LEFT){
                return (nextType == PipeType.HORIZONTAL || nextType == PipeType.ELBOW_UP_RIGHT || nextType == PipeType.ELBOW_DOWN_RIGHT);
            } else if (direction == Direction.UP){
                return (nextType == PipeType.VERTICAL || nextType == PipeType.ELBOW_DOWN_LEFT || nextType == PipeType.ELBOW_DOWN_RIGHT);
            }
        } else if (currentType == PipeType.ELBOW_DOWN_RIGHT){
            if (direction == Direction.RIGHT){
                return (nextType == PipeType.HORIZONTAL || nextType == PipeType.ELBOW_UP_LEFT || nextType == PipeType.ELBOW_DOWN_LEFT);
            } else if (direction == Direction.DOWN){
                return (nextType == PipeType.VERTICAL || nextType == PipeType.ELBOW_UP_LEFT || nextType == PipeType.ELBOW_UP_RIGHT);
            }
        } else if (currentType == PipeType.ELBOW_DOWN_LEFT){
            if (direction == Direction.LEFT){
                return (nextType == PipeType.HORIZONTAL || nextType == PipeType.ELBOW_UP_RIGHT || nextType == PipeType.ELBOW_DOWN_RIGHT);
            } else if (direction == Direction.DOWN){
                return (nextType == PipeType.VERTICAL || nextType == PipeType.ELBOW_UP_LEFT || nextType == PipeType.ELBOW_UP_RIGHT);
            }
        }
        return false;
    }

    @FXML
    protected void surrenderButton() {
        deleteCurrentPipes();
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
        deleteCurrentPipes();
    }

    private void deleteCurrentPipes(){
        graphL.removeAllEdges();
        for (Pipe pipe : pipesOnScreen) {
            int columnIndex = pipe.getCol();
            int rowIndex = pipe.getRow();
            board.getChildren().removeIf(node -> GridPane.getColumnIndex(node) != null &&
                    GridPane.getRowIndex(node) != null &&
                    GridPane.getColumnIndex(node) == columnIndex &&
                    GridPane.getRowIndex(node) == rowIndex
            );
        }
        pipesOnScreen.clear();
    }

    @FXML
    protected void menuButton() {
        MainMenu.hideWindow((Stage)text.getScene().getWindow());
        MainMenu.showWindow("hello-view", null);
    }

    private void getSourceAndDrainImage(){
        Image image = new Image("file:" + MainMenu.getFile("images/pipe_1.png").getPath());
        source = image;
        drain = image;
    }
}
