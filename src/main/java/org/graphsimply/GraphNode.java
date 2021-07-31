package org.graphsimply;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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

    private StringProperty name = new SimpleStringProperty("");
    private double xDrag;
    private double yDrag;
    private Label label;
    private GraphSimplyViewModel viewModel;
    private GraphSimplyController view;
    private static int defaultName = 65;
    public static String[] names = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
            "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    //TODO
    // Deletion moves back to last letter.

    // Simplifying the adjacency list.

    public GraphNode(double x, double y,
                     GraphSimplyViewModel viewModel,
                     GraphSimplyController view) {
        super(x, y, 30);
        String name = Character.toString((char) defaultName++);
        this.setFill(Color.WHITE);
        this.setStroke(Color.BLACK);
        this.setStrokeWidth(1.5);
        this.viewModel = viewModel;
        this.view = view;
        Label nameLabel = new Label(name);
        //region Setting name size and binding
        nameLabel.setMaxWidth(50);
        nameLabel.setMinWidth(50);
        nameLabel.setMaxHeight(20);
        nameLabel.setMinHeight(20);
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.layoutXProperty().bind(this.centerXProperty().subtract(25));
        nameLabel.layoutYProperty().bind(this.centerYProperty().subtract(10));
        nameLabel.setMouseTransparent(true);
        //endregion
        this.label = nameLabel;
        //region Set right click
        ContextMenu menu = new ContextMenu();
        MenuItem rename = new MenuItem("Rename");
        Pane pane = this.view.getPane();
        rename.setOnAction(e -> {
            System.out.println("Rename");
        });
        rename.setOnAction(e -> {
            TextInputDialog renameInput = new TextInputDialog(this.name.get());
            renameInput.setHeaderText("Enter new node name: ");
            renameInput.setGraphic(null);
            renameInput.showAndWait();
            String newName = renameInput.getEditor().getText();
            viewModel.updateName(this, newName);
//            this.name = new SimpleStringProperty(newName);
//            this.label.setText(newName);

        });
        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(e -> {
            // Remove from display
            pane.getChildren().removeAll(this, this.label);

            // Remove from model
            viewModel.removeNode(this);
            // Remove from VM
            for (GraphEdge edge : viewModel.getIncidentEdges(this)) {
                viewModel.removeEdgeFromNode(this, edge);
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
        int nameIndex = this.viewModel.createNewNode(this, name);
        this.name.bind(this.viewModel.getNodeNames().get(nameIndex));
        this.label.textProperty().bind(this.name);
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
                    // Already adds to the model via VM
                    GraphEdge edge = new GraphEdge(source, this, viewModel, view);
                    view.getPane().getChildren().add(0, edge);
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

    public StringProperty getName() {
        return name;
    }
    public String assignName() {
        for (int i = 0; i < names.length; i++) {
            if (!names[i].equals("/")) {
                String temp = names[i];
                names[i] = "/";
                return temp;
            }
        }
        return "";
    }


}
