module com.example.pipegame {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.pipegame to javafx.fxml;
    exports com.example.pipegame;
    exports com.example.pipegame.control;
    opens com.example.pipegame.control to javafx.fxml;
    exports com.example.pipegame.model;
    opens com.example.pipegame.model to javafx.fxml;
}