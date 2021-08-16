package org.graphsimply;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

import java.util.ArrayList;

public class GraphNode extends Circle {

    private StringProperty name = new SimpleStringProperty("");
    private double xDrag;
    private double yDrag;
    private Label label;
    private GraphSimplyViewModel viewModel;
    private GraphSimplyController view;

    //TODO
    // Deletion moves back to last letter.

    // Simplifying the adjacency list.

    public GraphNode(double x, double y,
                     GraphSimplyViewModel viewModel,
                     GraphSimplyController view) {
        super(x, y, 30);
        this.setFill(Color.WHITE);
        this.setStroke(Color.BLACK);
        this.setStrokeWidth(1.5);
        this.viewModel = viewModel;
        this.view = view;
        this.name = new SimpleStringProperty(viewModel.assignName());
        String name = this.name.get();
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
            TextInputDialog renameInput = new TextInputDialog(this.name.get());
            renameInput.getEditor().textProperty().addListener((ov, oldValue, newValue) -> {
                if (renameInput.getEditor().getText().length() > 5) {
                    String s = renameInput.getEditor().getText().substring(0, 5);
                    renameInput.getEditor().setText(s);
                }
            });
            renameInput.setHeaderText("Enter new node name: ");
            renameInput.setGraphic(null);
            renameInput.showAndWait();
            String newName = renameInput.getEditor().getText();
            boolean updated = viewModel.updateName(this, newName);
            if (!updated) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Error");
                alert.setContentText("Name already taken: " + newName);
                alert.showAndWait();
            }
//            this.name = new SimpleStringProperty(newName);
//            this.label.setText(newName);

        });
        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(e -> {
            // Remove from display
            view.removePrompt();
            for (GraphNode node : viewModel.getConnections().keySet()) {
                node.setFill(Color.WHITE);
            }
            pane.getChildren().removeAll(this, this.label);


            // Remove from VM
            ArrayList<GraphEdge> incidentEdges = viewModel.getIncidentEdges(this);
            if (incidentEdges != null) {
                ArrayList<Label> edgeLabels = new ArrayList<>();
                ArrayList<Polygon> arrowTips = new ArrayList<>();
                for (GraphEdge edge : incidentEdges) {
                    edgeLabels.add(edge.getLabel());
                    arrowTips.add(edge.getArrowTip());
                }
                pane.getChildren().removeAll(incidentEdges);
                pane.getChildren().removeAll(edgeLabels);
                pane.getChildren().removeAll(arrowTips);
                viewModel.removeEdgesFromNode(this, incidentEdges);
            }
            // Remove from model
            viewModel.removeNode(this);
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
        setCursor(Cursor.CLOSED_HAND);
    };
    private final EventHandler<MouseEvent> onDrag = (mouseEvent) -> {

        double priorCenterX = this.getCenterX();
        double priorCenterY = this.getCenterY();
        this.setCenterX(mouseEvent.getX() + xDrag);
        this.setCenterY(mouseEvent.getY() + yDrag);

        for (GraphNode node : viewModel.getNodes()) {
            if (!this.equals(node)) {
                Shape intersect = Shape.intersect(node, this);
                if (intersect.getLayoutBounds().getWidth() > 0) {
                    Point2D cx = new Point2D(priorCenterX, priorCenterY);
                    Point2D px = new Point2D(node.getCenterX(), node.getCenterY());
                    double d = cx.distance(px);
                    if (d > getRadius() + node.getRadius()) {
                        cx = pointAtDistance(cx, px, d - getRadius() - node.getRadius());
                    }
                    this.setCenterX(cx.getX());
                    this.setCenterY(cx.getY());
                    return;

                }
            }
        }
    };
    private final EventHandler<MouseEvent> cursorToHand = (mouseEvent) -> {
        setCursor(Cursor.OPEN_HAND);
    };
    private final EventHandler<MouseEvent> cursorToDefault = (mouseEvent) -> {
        setCursor(Cursor.DEFAULT);
    };
    // Helper for drag.
    private Point2D pointAtDistance(Point2D p1, Point2D p2, double distance) {
        double lineLength = p1.distance(p2);
        double t = distance / lineLength;
        double dx = ((1 - t) * p1.getX()) + (t * p2.getX());
        double dy = ((1 - t) * p1.getY()) + (t * p2.getY());
        return new Point2D(dx, dy);
    }
    //Edge creation handlers
    private final EventHandler<MouseEvent> onClick = (mouseEvent) -> {
        if (mouseEvent.getButton() != MouseButton.PRIMARY) return;
        if (viewModel.getCursor().equals("edge")) {
            GraphNode source = this.viewModel.getSource();
            // Set source and target accordingly
            if (source == null) {
                viewModel.setSource(this);
                DropShadow borderGlow = new DropShadow();
                borderGlow.setOffsetY(0f);
                borderGlow.setOffsetX(0f);
                borderGlow.setColor(Color.BLACK);
                borderGlow.setWidth(10);
                borderGlow.setHeight(10);
                this.setEffect(borderGlow);
            }
            else {
                // Disallow duplicate edges.
                boolean canMakeEdge = true;
                boolean edgeExistsBetween = false;
                for (GraphEdge edge : viewModel.getEdges()) {
                    if (edge.getSource().equals(source) && edge.getTarget().equals(this)) canMakeEdge = false;
                    if (edge.getSource().equals(this) && edge.getTarget().equals(source)) edgeExistsBetween = true;
                }
                if (canMakeEdge && !source.equals(this)) {
                    GraphEdge edge;
                    if (viewModel.getDirected() && edgeExistsBetween) {
                        edge = new GraphEdge(source, this, viewModel, view, true);
                    }
                    // Already adds to the model via VM
                    else {
                        edge = new GraphEdge(source, this, viewModel, view, false);
                    }
                    view.getPane().getChildren().add(0, edge);
                }
                // Regardless of if edge is made, reset effect and source.
                source.setEffect(null);
                viewModel.setSource(null);
            }
        }
        if (viewModel.getCursor().equals("dfs")) {
            viewModel.getDFS(this);
            mouseEvent.consume();
            // Reset back to previous
            viewModel.setCursor(viewModel.getPrevCursor());
            viewModel.toggleDrag();
            viewModel.toggleClick();
            view.removePrompt();
        }
        if (viewModel.getCursor().equals("bfs")) {
            viewModel.getBFS(this);
            mouseEvent.consume();
            // Reset back to previous
            viewModel.setCursor(viewModel.getPrevCursor());
            viewModel.toggleDrag();
            viewModel.toggleClick();
            view.removePrompt();
        }
        if (viewModel.getCursor().equals("shortestPaths")) {
            mouseEvent.consume();
            viewModel.getShortestPaths(this);
            viewModel.setCursor(viewModel.getPrevCursor());
            viewModel.toggleDrag();
            viewModel.toggleClick();
            view.removePrompt();
        }
    };

    public Label getLabel() {
        return label;
    }
    public void enableDrag() {
        this.addEventHandler(MouseEvent.MOUSE_PRESSED, onPress);
        this.addEventHandler(MouseEvent.MOUSE_DRAGGED, onDrag);
        this.addEventHandler(MouseEvent.MOUSE_ENTERED, cursorToHand);
        this.addEventHandler(MouseEvent.MOUSE_EXITED, cursorToDefault);
        this.addEventHandler(MouseEvent.MOUSE_RELEASED, cursorToHand);
    }
    public void disableDrag() {
        this.removeEventHandler(MouseEvent.MOUSE_PRESSED, onPress);
        this.removeEventHandler(MouseEvent.MOUSE_DRAGGED, onDrag);
        this.removeEventHandler(MouseEvent.MOUSE_ENTERED, cursorToHand);
        this.removeEventHandler(MouseEvent.MOUSE_EXITED, cursorToDefault);
        this.removeEventHandler(MouseEvent.MOUSE_RELEASED, cursorToHand);
    }
    public void enableClick() {
        this.addEventHandler(MouseEvent.MOUSE_CLICKED, onClick);
        System.out.println("Removing cursor");
        this.removeEventHandler(MouseEvent.MOUSE_ENTERED, cursorToHand);
        this.removeEventHandler(MouseEvent.MOUSE_EXITED, cursorToDefault);
        this.removeEventHandler(MouseEvent.MOUSE_RELEASED, cursorToHand);
    }
    public void disableClick() {
        this.removeEventHandler(MouseEvent.MOUSE_CLICKED, onClick);
    }

    public StringProperty getName() {
        return name;
    }
}
