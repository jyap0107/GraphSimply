package sample;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.Glow;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import javax.naming.Context;
import javax.xml.stream.EventFilter;
import java.util.ArrayList;

import static sample.Controller.canClick;

public class GraphNode extends Circle {
    private String name;
    private Label label;
    private ArrayList<GraphEdge> edgeList = new ArrayList<>();
    // For purposes of edge creation
    static GraphNode source;
    static GraphNode target;
    // For purposes of node dragging
    private double xDrag;
    private double yDrag;

    static Pane centerPane;
    static long lastTime = 0;

    // Setters and getters for name
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setLabel(Label label) {
        this.label = label;
    }
    public boolean removeEdge(GraphEdge edge) {
        return this.edgeList.remove(edge);
    }

    // Mouse Events
    private final EventHandler<MouseEvent> onPress = (mouseEvent) -> {
        if (!canClick) return;
        xDrag = this.getCenterX() - mouseEvent.getX();
        yDrag = this.getCenterY() - mouseEvent.getY();
    };
    private final EventHandler<MouseEvent> onDrag = (mouseEvent) -> {
        if (!canClick) return;
        this.setCenterX(mouseEvent.getX() + xDrag);
        this.setCenterY(mouseEvent.getY() + yDrag);
    };
    private final EventHandler<MouseEvent> onDoubleClick = (mouseEvent) -> {
        long diff = 0;
        long currentTime = System.currentTimeMillis();

        if (lastTime != 0 && currentTime != 0){
            diff = currentTime - lastTime;
            if (diff <= 215) System.out.println("Double");
        }
        lastTime = currentTime;
    };

    // On click, if edge selected, records starting edge and ending edge as
    // necessary.
    private EventHandler<MouseEvent> onClick = (mouseEvent) -> {
        if (mouseEvent.getButton() != MouseButton.PRIMARY) return;
        if (Controller.cursor.equals("edge")) {
            if (source == null) {
                source = this;
                Glow glow = new Glow();
                glow.setLevel(1);
                this.setEffect(glow);
            } else {
                target = this;
                System.out.println("Edge click");
                // Don't make a duplicate edge.
                boolean canMakeEdge = true;
                for (GraphEdge edge : edgeList) {
                    if (edge.getSource().equals(source) && edge.getTarget().equals(target)) canMakeEdge = false;
                }
                if (canMakeEdge) {
                    System.out.println("yes");
                    GraphEdge newEdge = new GraphEdge(source, target);
                    centerPane.getChildren().add(newEdge);
                    source.edgeList.add(newEdge);
                    target.edgeList.add(newEdge);
                    Controller.edges.add(newEdge);
                }
                source.setEffect(null);
                target.setEffect(null);
                source = null;
                target = null;
            }
        }
    };

    // Create new GraphNode given name and coordinates
    public GraphNode(double x, double y, String name) {
        super(x, y, 30);
        this.name = name;
        this.setFill(Color.WHITE);
        this.setStroke(Color.BLACK);
        this.setStrokeWidth(1.5);

        //region Set right click
        ContextMenu menu = new ContextMenu();
        MenuItem rename = new MenuItem("Rename");
        rename.setOnAction(e -> {
            System.out.println("Rename");
        });
        rename.setOnAction(e -> {
            TextInputDialog renameInput = new TextInputDialog(this.name);
            renameInput.setHeaderText("Enter new node name: ");
            renameInput.setGraphic(null);
            renameInput.showAndWait();
            String newName = renameInput.getEditor().getText();
            this.name = newName;
            this.label.setText(newName);
        });
        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(e -> {
            centerPane.getChildren().removeAll(this, this.label);
            Controller.nodes.remove(this);
            for (GraphEdge edge : this.edgeList) {
                Controller.edges.remove(edge);
                centerPane.getChildren().remove(edge);
            }
        });
        this.addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
            menu.show(centerPane, event.getScreenX(), event.getScreenY());
            event.consume();
        });
        centerPane.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            menu.hide();
        });
        menu.getItems().addAll(rename, delete);
        //endregion
        this.addEventHandler(MouseEvent.MOUSE_CLICKED, onDoubleClick);
      }

    // Enable dragging
    public void enableDrag() {
        this.addEventHandler(MouseEvent.MOUSE_PRESSED, onPress);
        this.addEventHandler(MouseEvent.MOUSE_DRAGGED, onDrag);
    }
    //Disable dragging
    public void disableDrag() {
        this.removeEventHandler(MouseEvent.MOUSE_PRESSED, onPress);
        this.removeEventHandler(MouseEvent.MOUSE_DRAGGED, onDrag);
    }
    public void enableEdgeOnClick() {
        this.setOnMouseClicked(onClick);
    }
    public void disableEdgeOnClick() {
        this.removeEventHandler(MouseEvent.MOUSE_CLICKED, onClick);
    }
    public static void deselectNode() {
        if (source != null) source.setEffect(null);
        if (target != null) target.setEffect(null);
        source = null;
        target = null;
    }
}
