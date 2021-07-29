module org.graphsimply {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens org.graphsimply to javafx.fxml;
    exports org.graphsimply to javafx.graphics;
}