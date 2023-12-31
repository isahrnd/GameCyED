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
    private boolean isVertical;
    public static int selectedGraphMode;
    private final ArrayList<Pipe> pipesOnScreen = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gc = canvas.getGraphicsContext2D();
        canvas.setFocusTraversable(true);
        int direction = (int)(Math.random() * 2) + 1;
        isVertical = direction == 1;
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
        blockedCells = new boolean[board.getRowCount()][board.getColumnCount()];
        Random random = new Random();
        int blockedCount = 0;
        while (blockedCount < 60) {
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
        int[] pos = generateSourceAndDrainCols(isVertical);
        sourceVertex = getVertexFromCell(isVertical ? pos[0] : 0, isVertical ? 0 : pos[0]);
        drainVertex = getVertexFromCell(isVertical ? pos[1] : board.getColumnCount()-1, isVertical ? board.getRowCount()-1 : pos[1]);
    }

    private void paintFountainAndDraw() {
        gc.drawImage(source, isVertical ? allowedCoordinates(sourceVertex.getData().getCol()) : 0, isVertical ? 0 : allowedCoordinates(sourceVertex.getData().getRow()), 35, 35);
        gc.drawImage(drain, isVertical ? allowedCoordinates(drainVertex.getData().getCol()) : 565, isVertical ? 565 : allowedCoordinates(drainVertex.getData().getRow()), 35, 35);
    }

    private int allowedCoordinates(int position){
        int[] coordinates = {35,71,106,141,176,211,246,282,318,353,388,424,459,494,530};
        return coordinates[position];
    }

    private int[] generateSourceAndDrainCols(boolean isVertical) {
        Random random = new Random();
        int fountain_pos;
        do {
            fountain_pos = random.nextInt(board.getColumnCount());
        } while (isCellBlocked(isVertical ? 0 : fountain_pos, isVertical ? fountain_pos : 0));
        int drain_pos;
        do {
            drain_pos = random.nextInt(board.getColumnCount());
        } while (isCellBlocked(isVertical ? board.getRowCount()-1 : drain_pos, isVertical ? drain_pos : board.getRowCount()-1));
        return new int[]{fountain_pos, drain_pos};
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

            ArrayList<Vertex<Pipe>> myPath = path();
            int myPathSize = myPath.size();
            deleteCurrentPipes();
            buildGraphWithoutPipes();
            highlightPath(myPath, Color.AQUA);
            int shortestPathSize = shortestPath().size();
            if (myPathSize == shortestPathSize){
                msg += "\nYou found one of the fastest ways! +1000 pts";
                score += 1000;
            }

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
        PipeType drainType = drainVertex.getData().getType();
        if (isVertical){
            return (sourceType == PipeType.ELBOW_UP_LEFT || sourceType == PipeType.ELBOW_DOWN_RIGHT || sourceType == PipeType.VERTICAL) &&
                   (drainType == PipeType.ELBOW_DOWN_LEFT || drainType == PipeType.ELBOW_DOWN_RIGHT || drainType == PipeType.VERTICAL);
        } else {
            return (sourceType == PipeType.ELBOW_DOWN_LEFT || sourceType == PipeType.ELBOW_UP_LEFT || sourceType == PipeType.HORIZONTAL) &&
                   (drainType == PipeType.ELBOW_DOWN_RIGHT || drainType == PipeType.ELBOW_UP_RIGHT || drainType == PipeType.HORIZONTAL);
        }
    }

    private boolean validatePipeConnections(ArrayList<Vertex<Pipe>> path) {
        ArrayList<Vertex<Pipe>> covered = new ArrayList<>();
        Vertex<Pipe> currentVertex = path.get(0);
        while (currentVertex != drainVertex){
            Vertex<Pipe> nextVertex = null;
            for (Vertex<Pipe> neighbor : currentVertex.getNeighbors()) {
                if (!covered.contains(neighbor)) {
                    Direction direction = getPipeDirection(currentVertex, neighbor);
                    if (isValidPipeConnection(currentVertex.getData(), neighbor.getData(), direction)) {
                        nextVertex = neighbor;
                        break;
                    }
                }
            }
            if (nextVertex == null) {
                return false;
            }
            covered.add(currentVertex);
            currentVertex = nextVertex;
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
            highlightPath(shortestPath(), Color.YELLOW);
            validateButton.setDisable(true);
            resetButton.setDisable(true);
            giveUpButton.setDisable(true);
            handleGridClickEnabled = false;
        }
    }

    private ArrayList<Vertex<Pipe>> shortestPath(){
        return graph.dijkstra(sourceVertex,drainVertex);
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

    private void highlightPath(ArrayList<Vertex<Pipe>> path, Color color) {
        for (Vertex<Pipe> vertex : path) {
            int columnIndex = vertex.getData().getCol();
            int rowIndex = vertex.getData().getRow();
            Rectangle rectangle = new Rectangle(board.getWidth() / board.getColumnCount(), board.getHeight() / board.getRowCount());
            rectangle.setFill(color);
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
        Image image = new Image("file:" + MainMenu.getFile("images/pipe_"+(isVertical? "7":"8")+".png").getPath());
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