package sample;


import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.beans.property.DoubleProperty;
import javafx.scene.text.TextBoundsType;

import javax.xml.stream.EventFilter;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {




    private ArrayList<ArrayList<Integer>> graphState =
            new ArrayList<ArrayList<Integer>>();
    static ArrayList<GraphNode> nodes = new ArrayList<GraphNode>();
    static ArrayList<GraphEdge> edges = new ArrayList<GraphEdge>();
    static boolean canClick = true;
    private boolean directed = true;


    static String cursor = "select";

    @FXML
    private RadioButton directedButton, undirectedButton;
    @FXML
    private ToggleButton selectButton, nodeButton, edgeButton;
    @FXML
    private Pane centerPane;

    private GraphicsContext gc;

    public Pane getCenterPane() {
        return centerPane;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        directedButton.setOnAction(e -> directed = true);
        undirectedButton.setOnAction(e -> directed = false);
        //region Pane clip
        Rectangle clip = new Rectangle(centerPane.getWidth(),
                centerPane.getHeight());
        clip.heightProperty().bind(centerPane.heightProperty());
        clip.widthProperty().bind(centerPane.widthProperty());
        centerPane.setClip(clip);
        GraphNode.centerPane = centerPane;
        //endregion

    }
    public void setCursorToNode() {
        cursor = "node";
        GraphNode.source = null;
        GraphNode.target = null;
        for (GraphNode node : nodes) {
            node.disableDrag();
        }

    }
    public void setCursorToSelect() {
        cursor = "select";
        GraphNode.source = null;
        GraphNode.target = null;
        for (GraphNode node : nodes) {
            node.disableEdgeOnClick();
            node.enableDrag();
        }
    }
    public void setCursorToEdge() {
        cursor = "edge";
//        GraphNode a = new GraphNode(100, 100, "");
//        GraphNode b = new GraphNode(200, 200, "");
//        centerPane.getChildren().add(a);
//        centerPane.getChildren().add(b);
//        nodes.add(a);
//        nodes.add(b);
        for (GraphNode node : nodes) {
            node.disableDrag();
            node.enableEdgeOnClick();
        }
    }
    public void createNode(MouseEvent e) {
        if (cursor.equals("node") && e.getButton()== MouseButton.PRIMARY && canClick) {
            // Create Node and Label
            double x = e.getX();
            double y = e.getY();
            GraphNode node = new GraphNode(x, y, "N/A");
            Label nodeName = new Label("N/A");
            //region Setting name size and binding
            nodeName.setMaxWidth(50);
            nodeName.setMinWidth(50);
            nodeName.setMaxHeight(20);
            nodeName.setMinHeight(20);
            nodeName.setAlignment(Pos.CENTER);
            nodeName.layoutXProperty().bind(node.centerXProperty().subtract(25));
            nodeName.layoutYProperty().bind(node.centerYProperty().subtract(10));
            //endregion

            nodeName.setMouseTransparent(true);

            //Add to panes and lists.
            centerPane.getChildren().addAll(node, nodeName);
            node.setLabel(nodeName);
            nodes.add(node);
        }
//        if (cursor.equals("edge") && GraphNode.source != null) {
//            GraphNode.source = null;
//            GraphNode.deselectNode();
//            System.out.println("pane click");
//        }

    }
}
