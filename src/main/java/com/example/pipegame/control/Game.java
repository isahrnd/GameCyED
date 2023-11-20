package com.example.pipegame.control;

import com.example.pipegame.MainMenu;
import com.example.pipegame.model.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;

public class Game implements Initializable {

    @FXML
    private Label vText;
    @FXML
    private GridPane board;
    @FXML
    private Canvas canvas;
    @FXML
    private Button validateButton;
    @FXML
    private Button resetButton;
    @FXML
    private Button giveUpButton;
    private GraphicsContext gc;
    private Vertex<Pipe> sourceVertex;
    private Vertex<Pipe> drainVertex;
    private iGraph<Pipe> graph;
    private boolean handleGridClickEnabled = true;
    private int currentImageIndex = 1;
    private boolean[][] blockedCells;
    private Image source, drain;
    private Calendar startTime;
    public static int selectedGraphMode;
    private final ArrayList<Pipe> pipesOnScreen = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gc = canvas.getGraphicsContext2D();
        canvas.setFocusTraversable(true);
        getSourceAndDrainImage();
        if (selectedGraphMode == 1) {
            graph = new AdjacencyListGraph<>();
        } else if (selectedGraphMode == 2) {
            graph = new AdjacencyMatrixGraph<>();
        }
        initializeGame();
        board.setOnMouseClicked(this::handleGridClick);
    }

    private void initializeGame() {
        generateBlockedCells();
        Platform.runLater(() -> {
            initializeGraph();
            addSourceAndDrainVertex();
            buildGraphWithoutPipes();
            if (path().contains(drainVertex)) {
                startTime = Calendar.getInstance();
                paintFountainAndDraw();
                graph.removeAllEdges();
            } else {
                MainMenu.showAlert(Alert.AlertType.WARNING,"Warning","Game without solution","Sorry, the generated game has no solution. Please try again.");
                MainMenu.hideWindow((Stage)vText.getScene().getWindow());
                MainMenu.showWindow("hello-view", null);
            }
        });
    }

    private ArrayList<Vertex<Pipe>> path() {
        return graph.bfs(sourceVertex);
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
        for (int row = 0; row < board.getRowCount(); row++) {
            for (int col = 0; col < board.getColumnCount(); col++) {
                boolean isBlocked = blockedCells[row][col];
                if (!isBlocked) {
                    Vertex<Pipe> vertex = new Vertex<>(new Pipe(-1, row, col));
                    graph.addVertex(vertex);
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
    }

    private void paintFountainAndDraw() {
        gc.drawImage(source, allowedXCoordinates(sourceVertex.getData().getCol()), 0, 37, 23);
        gc.drawImage(drain, allowedXCoordinates(drainVertex.getData().getCol()), 376, 37, 24);
    }

    private int allowedXCoordinates(int position){
        int[] x_coordinates = {46,82,117,152,188,223,258,293,329,364};
        return x_coordinates[position];
    }

    private int[] generateSourceAndDrainCols() {
        Random random = new Random();
        int fountain_col;
        do {
            fountain_col = random.nextInt(board.getColumnCount());
        } while (isCellBlocked(0, fountain_col));
        int drain_col;
        do {
            drain_col = random.nextInt(board.getColumnCount());
        } while (isCellBlocked(9, drain_col));
        return new int[]{fountain_col, drain_col};
    }

    private boolean isCellBlocked(int row, int col) {
        return blockedCells[row][col];
    }

    private void handleGridClick(MouseEvent event) {
        if (handleGridClickEnabled){
            int columnIndex = (int) (event.getX() / (board.getWidth() / board.getColumnCount()));
            int rowIndex = (int) (event.getY() / (board.getHeight() / board.getRowCount()));
            if (isCellBlocked(rowIndex, columnIndex)) {
                return;
            }
            // update board
            Pipe existingPipe = getPipeInCell(columnIndex, rowIndex);
            if (existingPipe != null) {
                pipesOnScreen.remove(existingPipe);
                board.getChildren().removeIf(node -> GridPane.getColumnIndex(node) != null &&
                        GridPane.getRowIndex(node) != null &&
                        GridPane.getColumnIndex(node) == columnIndex &&
                        GridPane.getRowIndex(node) == rowIndex
                );
            } else {
                currentImageIndex = 1;
            }
            Pipe pipe = new Pipe(currentImageIndex, rowIndex, columnIndex);
            pipesOnScreen.add(pipe);
            showImageInBoard(pipe.getImage(),columnIndex,rowIndex);
            currentImageIndex = (currentImageIndex % 6) + 1;
            // update vertex
            Vertex<Pipe> currentVertex = getVertexFromCell(columnIndex, rowIndex);
            if (currentVertex != null) {
                currentVertex.setData(pipe);
            }
        }
    }

    private void connectVerticesWithPipes() {
        for (Pipe pipe : pipesOnScreen){
            Vertex<Pipe> currentVertex = getVertexFromCell(pipe.getCol(), pipe.getRow());
            if (currentVertex != null) {
                connectWithNeighbors(currentVertex);
            }
        }
    }

    private void connectWithNeighbors(Vertex<Pipe> vertex) {
        boolean isVertexWithPipe = vertex.getData().getType() != null;
        int row = vertex.getData().getRow();
        int col = vertex.getData().getCol();
        // connect with upper neighbor
        if (row > 0) {
            Vertex<Pipe> neighbor = getVertexFromCell(col, row - 1);
            if (neighbor != null) {
                if (isVertexWithPipe) {
                    Pipe upPipe = getPipeInCell(col, row - 1);
                    if (upPipe != null) {
                        graph.addEdge(vertex, neighbor, 1);
                    }
                } else {
                    graph.addEdge(vertex, neighbor, 1);
                }
            }
        }
        // connect with lower neighbor
        if (row < board.getRowCount() - 1) {
            Vertex<Pipe> neighbor = getVertexFromCell(col, row + 1);
            if (neighbor != null) {
                if (isVertexWithPipe) {
                    Pipe downPipe = getPipeInCell(col, row + 1);
                    if (downPipe != null) {
                        graph.addEdge(vertex, neighbor, 1);
                    }
                } else {
                    graph.addEdge(vertex, neighbor, 1);
                }
            }
        }
        // connect with left neighbor
        if (col > 0) {
            Vertex<Pipe> neighbor = getVertexFromCell(col - 1, row);
            if (neighbor != null) {
                if (isVertexWithPipe) {
                    Pipe leftPipe = getPipeInCell(col - 1, row);
                    if (leftPipe != null) {
                        graph.addEdge(vertex, neighbor, 1);
                    }
                } else {
                    graph.addEdge(vertex, neighbor, 1);
                }
            }
        }
        // connect with right neighbor
        if (col < board.getColumnCount() - 1) {
            Vertex<Pipe> neighbor = getVertexFromCell(col + 1, row);
            if (neighbor != null) {
                if (isVertexWithPipe) {
                    Pipe downPipe = getPipeInCell(col + 1, row);
                    if (downPipe != null) {
                        graph.addEdge(vertex, neighbor, 1);
                    }
                } else {
                    graph.addEdge(vertex, neighbor, 1);
                }
            }
        }
    }

    private Vertex<Pipe> getVertexFromCell(int columnIndex, int rowIndex) {
        for (Vertex<Pipe> vertex : graph.getVertices()) {
            if (vertex.getData().getRow() == rowIndex && vertex.getData().getCol() == columnIndex) {
                return vertex;
            }
        }
        return null;
    }

    private Pipe getPipeInCell(int columnIndex, int rowIndex) {
        for (Pipe pipe : pipesOnScreen) {
            if (pipe.getCol() == columnIndex && pipe.getRow() == rowIndex) {
                return pipe;
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
    protected void onValidateButton() {
        if (validatePath()){
            Calendar finalTime = Calendar.getInstance();
            int seconds = calculateTime(finalTime);
            int score = calculateScore(seconds);
            String msg = "Number of pipes used: " + pipesOnScreen.size();
            msg += "\nTime: " + seconds + " sec.";
            msg += "\nFinal score: " + score;
            MainMenu.showAlert(Alert.AlertType.INFORMATION,"Information","Congratulations! You won the game",msg);
            MainMenu.hideWindow((Stage)vText.getScene().getWindow());
            MainMenu.showWindow("hello-view", null);
        } else {
            MainMenu.showAlert(Alert.AlertType.ERROR,"Error","Your solution is not correct.",null);
            deleteCurrentPipes();
        }
    }

    private boolean validatePath(){
        if (validateSourceAndDrain()){
            connectVerticesWithPipes();
            if (path().contains(drainVertex)){
                return validatePipeConnections(path());
            }
        }
        return false;
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
                return false;
            }
        }
        return true;
    }

    private Direction getPipeDirection(Vertex<Pipe> currentVertex, Vertex<Pipe> nextVertex) {
        int currentRow = currentVertex.getData().getRow();
        int currentCol = currentVertex.getData().getCol();
        int nextRow = nextVertex.getData().getRow();
        int nextCol = nextVertex.getData().getCol();
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
    protected void onGiveUpButton() {
        Optional<ButtonType> result = MainMenu.showAlert(Alert.AlertType.CONFIRMATION, "Confirmation", "Are you sure you want to give up?", null);
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteCurrentPipes();
            buildGraphWithoutPipes();
            ArrayList<Vertex<Pipe>> shortestPath = graph.dijkstra(sourceVertex, drainVertex);
            highlightShortestPath(shortestPath);
            validateButton.setDisable(true);
            resetButton.setDisable(true);
            giveUpButton.setDisable(true);
            handleGridClickEnabled = false;
        }
    }

    private void buildGraphWithoutPipes() {
        for (int row = 0; row < board.getRowCount(); row++) {
            for (int col = 0; col < board.getColumnCount(); col++) {
                Vertex<Pipe> currentVertex = getVertexFromCell(row, col);
                if (currentVertex != null){
                    connectWithNeighbors(currentVertex);
                }
            }
        }
    }

    private void highlightShortestPath(ArrayList<Vertex<Pipe>> shortestPath) {
        for (Vertex<Pipe> vertex : shortestPath) {
            int columnIndex = vertex.getData().getCol();
            int rowIndex = vertex.getData().getRow();
            Rectangle rectangle = new Rectangle(board.getWidth() / board.getColumnCount(), board.getHeight() / board.getRowCount());
            rectangle.setFill(Color.YELLOW);
            board.add(rectangle, columnIndex, rowIndex);
        }
    }

    @FXML
    protected void onResetButton() {
        deleteCurrentPipes();
    }

    private void deleteCurrentPipes(){
        graph.removeAllEdges();
        for (Pipe pipe : pipesOnScreen) {
            int columnIndex = pipe.getCol();
            int rowIndex = pipe.getRow();
            board.getChildren().removeIf(node -> GridPane.getColumnIndex(node) != null &&
                    GridPane.getRowIndex(node) != null &&
                    GridPane.getColumnIndex(node) == columnIndex &&
                    GridPane.getRowIndex(node) == rowIndex
            );
            Vertex<Pipe> currentVertex = getVertexFromCell(columnIndex, rowIndex);
            if (currentVertex != null) {
                currentVertex.setData(new Pipe(-1,rowIndex,columnIndex));
            }
        }
        pipesOnScreen.clear();
    }

    @FXML
    protected void onMenuButton() {
        Optional<ButtonType> result = handleGridClickEnabled
                ? MainMenu.showAlert(Alert.AlertType.CONFIRMATION, "Confirmation", "Are you sure you want to return to the menu?", null)
                : Optional.of(ButtonType.OK);

        if (result.isPresent() && result.get() == ButtonType.OK) {
            MainMenu.hideWindow((Stage) vText.getScene().getWindow());
            MainMenu.showWindow("hello-view", null);
        }
    }

    private void getSourceAndDrainImage(){
        Image image = new Image("file:" + MainMenu.getFile("images/pipe_7.png").getPath());
        source = image;
        drain = image;
    }

    private int calculateScore(int seconds){
        return (100 - pipesOnScreen.size()) * 10 - seconds;
    }

    private int calculateTime(Calendar finalTime){
        return (int) ((finalTime.getTimeInMillis() - startTime.getTimeInMillis()) / 1000);
    }
}