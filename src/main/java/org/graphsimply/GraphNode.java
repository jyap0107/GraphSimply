package org.graphsimply;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
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

import java.util.ArrayList;

public class GraphNode extends Circle {

    private String name;
    private double xDrag;
    private double yDrag;
    private Label label;
    private GraphSimplyViewModel viewModel;
    private GraphSimplyController view;
    // Simplifying the adjacency list.

    public GraphNode(double x, double y, String name,
                     GraphSimplyViewModel viewModel,
                     GraphSimplyController view) {
        super(x, y, 30);
        this.name = name;
        this.setFill(Color.WHITE);
        this.setStroke(Color.BLACK);
        this.setStrokeWidth(1.5);
        this.viewModel = viewModel;
        this.view = view;
        Label nodeName = new Label("N/A");
        //region Setting name size and binding
        nodeName.setMaxWidth(50);
        nodeName.setMinWidth(50);
        nodeName.setMaxHeight(20);
        nodeName.setMinHeight(20);
        nodeName.setAlignment(Pos.CENTER);
        nodeName.layoutXProperty().bind(this.centerXProperty().subtract(25));
        nodeName.layoutYProperty().bind(this.centerYProperty().subtract(10));
        nodeName.setMouseTransparent(true);
        //endregion
        this.label = nodeName;
        //region Set right click
        ContextMenu menu = new ContextMenu();
        MenuItem rename = new MenuItem("Rename");
        Pane pane = this.view.getPane();
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
            // Remove from display
            pane.getChildren().removeAll(this, this.label);

            // Remove from VM and then model
            this.viewModel.removeNode(this);
            for (GraphEdge edge : this.nodeEdges) {
                this.viewModel.removeEdge(edge);
                pane.getChildren().remove(edge);
            }
        });
        this.addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
            menu.show(pane, event.getScreenX(), event.getScreenY());
            event.consume();
        });
        pane.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            menu.hide();
        });
        menu.getItems().addAll(rename, delete);
        //endregion
        // Create data class for it
        this.viewModel.createNewNode(this);
    }
    //Dragging handlers
    private final EventHandler<MouseEvent> onPress = (mouseEvent) -> {
        this.toFront();
        this.label.toFront();
        xDrag = this.getCenterX() - mouseEvent.getX();
        yDrag = this.getCenterY() - mouseEvent.getY();
    };
    private final EventHandler<MouseEvent> onDrag = (mouseEvent) -> {
        this.setCenterX(mouseEvent.getX() + xDrag);
        this.setCenterY(mouseEvent.getY() + yDrag);
    };
    //Edge creation handlers
    private final EventHandler<MouseEvent> onClick = (mouseEvent) -> {
        if (mouseEvent.getButton() != MouseButton.PRIMARY) return;
        if (viewModel.getCursor().equals("edge")) {
            GraphNode source = this.viewModel.getSource();
            // Set source and target accordingly
            if (source == null) {
                viewModel.setSource(this);
                Glow glow = new Glow();
                glow.setLevel(1);
                this.setEffect(glow);
            }
            else {
                System.out.println("Made");
                // Disallow duplicate edges.
                boolean canMakeEdge = true;
                for (GraphEdge edge : viewModel.getEdges()) {
                    if (edge.getSource().equals(source) && edge.getTarget().equals(this)) canMakeEdge = false;
                }
                if (canMakeEdge) {
                    GraphEdge edge = new GraphEdge(source, this, viewModel, view);
                    view.getPane().getChildren().add(0, edge);
                    viewModel.addEdgeToNode(source, edge);
                    viewModel.addEdgeToNode(this, edge);
                }
                // Regardless of if edge is made, reset effect and source.
                source.setEffect(null);
                viewModel.setSource(null);
            }
        }
        if (viewModel.getCursor().equals("dfs")) {
            viewModel.setDfsSource(this);
        }
    };

    public Label getLabel() {
        return label;
    }
    public void enableDrag() {
        this.addEventHandler(MouseEvent.MOUSE_PRESSED, onPress);
        this.addEventHandler(MouseEvent.MOUSE_DRAGGED, onDrag);
    }
    public void disableDrag() {
        this.removeEventHandler(MouseEvent.MOUSE_PRESSED, onPress);
        this.removeEventHandler(MouseEvent.MOUSE_DRAGGED, onDrag);
    }
    public void enableEdgeCreation() {
        this.addEventHandler(MouseEvent.MOUSE_CLICKED, onClick);
    }
    public void disableEdgeCreation() {
        this.removeEventHandler(MouseEvent.MOUSE_CLICKED, onClick);
    }

    public String getName() {
        return name;
    }

}
