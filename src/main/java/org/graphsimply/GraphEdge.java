package org.graphsimply;

import javafx.beans.binding.Bindings;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

public class GraphEdge extends Line {
    private GraphNode source;
    private GraphNode target;
    private GraphSimplyController view;
    private GraphSimplyViewModel viewModel;
    private static int edgeId = 0;

    public GraphEdge(GraphNode source, GraphNode target, GraphSimplyViewModel viewModel, GraphSimplyController view) {
        this.source = source;
        this.target = target;
        this.view = view;
        this.viewModel = viewModel;
        this.setFill(Color.BLACK);
        //region Edge relocation
        this.startXProperty().bind(Bindings.createDoubleBinding(() -> {
                // Collision detection, make edge disappear.
                Shape intersect = Shape.intersect(source, target);
                if (intersect.getBoundsInLocal().getWidth() != -1) {
                    this.setStroke(Color.TRANSPARENT);
                } else {
                    this.setStroke(Color.BLACK);
                }
                // Calculate offset
                double slope =
                        (target.getCenterY() - source.getCenterY()) / (target.getCenterX() - source.getCenterX());
                double offset = Math.cos(Math.atan(slope)) * source.getRadius();
                // Edge appears at offset from center. Repeat for other properties.
                return target.getCenterX() > source.getCenterX() ?
                        source.getCenterX() + offset : source.getCenterX() - offset;
            }, source.centerXProperty(), source.centerYProperty(),
            target.centerXProperty(), target.centerYProperty(),
            source.radiusProperty(), source.boundsInParentProperty(),
            target.boundsInParentProperty()));
        this.startYProperty().bind(Bindings.createDoubleBinding(() -> {
                Shape intersect = Shape.intersect(source, target);
                if (intersect.getBoundsInLocal().getWidth() != -1) {
                    this.setStroke(Color.TRANSPARENT);
                } else {
                    this.setStroke(Color.BLACK);
                }

                double slope =
                        (target.getCenterY() - source.getCenterY()) / (target.getCenterX() - source.getCenterX());
                double offset = Math.sin(Math.atan(slope)) * source.getRadius();

                return target.getCenterX() >= source.getCenterX() ?
                        source.getCenterY() + offset :
                        source.getCenterY() - offset;
            }, source.centerXProperty(), source.centerYProperty(),
            target.centerXProperty(), target.centerYProperty(),
            source.radiusProperty(), source.boundsInParentProperty(),
            target.boundsInParentProperty()));
        this.endXProperty().bind(Bindings.createDoubleBinding(() -> {
                Shape intersect = Shape.intersect(source, target);
                if (intersect.getBoundsInLocal().getWidth() != -1) {
                    this.setStroke(Color.TRANSPARENT);
                } else {
                    this.setStroke(Color.BLACK);
                }

                double slope =
                        (target.getCenterY() - source.getCenterY()) / (target.getCenterX() - source.getCenterX());
                double offset = Math.cos(Math.atan(slope)) * target.getRadius();

                return target.getCenterX() >= source.getCenterX() ?
                        target.getCenterX() - offset :
                        target.getCenterX() + offset;
            }, source.centerXProperty(), source.centerYProperty(),
            target.centerXProperty(), target.centerYProperty(),
            target.radiusProperty(), source.boundsInParentProperty(),
            target.boundsInParentProperty()));
        this.endYProperty().bind(Bindings.createDoubleBinding(() -> {
                Shape intersect = Shape.intersect(source, target);
                if (intersect.getBoundsInLocal().getWidth() != -1) {
                    this.setStroke(Color.TRANSPARENT);
                } else {
                    this.setStroke(Color.BLACK);
                }
                double slope =
                        (target.getCenterY() - source.getCenterY()) / (target.getCenterX() - source.getCenterX());
                double offset = Math.sin(Math.atan(slope)) * target.getRadius();

                return target.getCenterX() > source.getCenterX() ?
                        target.getCenterY() - offset :
                        target.getCenterY() + offset;
            }, source.centerXProperty(), source.centerYProperty(),
            target.centerXProperty(), target.centerYProperty(),
            target.radiusProperty(), source.boundsInParentProperty(),
            target.boundsInParentProperty()));
        //endregion
        this.viewModel.createNewEdge(this);

//        //region Right click
//        ContextMenu menu = new ContextMenu();
//        MenuItem changeWeight = new MenuItem("Change Weight");
//        MenuItem delete = new MenuItem("Delete");
//        changeWeight.setOnAction(e -> {
//
//        });
//        delete.setOnAction(e -> {
//            this.source.removeEdge(this);
//            this.target.removeEdge(this);
//            this.view.getPane().getChildren().remove(this);
//        });
//        Line clickableLine = new Line();
//        clickableLine.startXProperty().bind(this.startXProperty());
//        clickableLine.startYProperty().bind(this.startYProperty());
//        clickableLine.endXProperty().bind(this.endXProperty());
//        clickableLine.endYProperty().bind(this.endYProperty());
//        clickableLine.setStrokeWidth(10);
//        clickableLine.setOpacity(0);
//        clickableLine.addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED,
//                event -> {
//                    menu.show(GraphNode.centerPane, event.getScreenX(),
//                            event.getScreenY());
//                    event.consume();
//                });
//        GraphNode.centerPane.addEventHandler(MouseEvent.MOUSE_PRESSED,
//                event -> {
//                    menu.hide();
//                });
//        GraphNode.centerPane.getChildren().add(clickableLine);
//        menu.getItems().addAll(changeWeight, delete);

    }
    public GraphNode getSource() {
        return source;
    }
    public GraphNode getTarget() {
        return target;
    }
    public int getEdgeId() { return edgeId; }
}
