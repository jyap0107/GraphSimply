package org.graphsimply;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

import java.util.List;


public class GraphEdge extends Line {
    private GraphNode source;
    private GraphNode target;
    private GraphSimplyController view;
    private GraphSimplyViewModel viewModel;
    private IntegerProperty weight = new SimpleIntegerProperty(0);
    private Label label;
    private IntegerProperty index;
    private Polygon arrowTip;

    public GraphEdge(GraphNode source, GraphNode target, GraphSimplyViewModel viewModel, GraphSimplyController view) {
        this.source = source;
        this.target = target;
        this.view = view;
        this.viewModel = viewModel;
        this.setFill(Color.BLACK);
        this.label = new Label(weight.get() + "");
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
        //region label
        this.label.layoutXProperty().bind(Bindings.createDoubleBinding(() -> Math.abs(this.getEndX() + this.getStartX())/2-15, this.startXProperty(), this.endXProperty()));
        this.label.layoutYProperty().bind(Bindings.createDoubleBinding(() -> Math.abs(this.getEndY() + this.getStartY())/2-10,
                this.startYProperty(), this.endYProperty()));
        this.view.getPane().getChildren().add(this.label);
        this.label.setMaxWidth(30);
        this.label.setMinWidth(30);
        this.label.setMaxHeight(20);
        this.label.setMinHeight(20);
        this.label.setAlignment(Pos.CENTER);
        this.label.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        this.label.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.DOTTED, null, null)));
        //endregion
        //region label rightclick
        ContextMenu menu = new ContextMenu();
        Pane pane = this.view.getPane();
        MenuItem changeWeight = new MenuItem("Change weight");
        changeWeight.setOnAction(e -> {
            TextInputDialog weightInput = new TextInputDialog(this.weight.get() + "");
            weightInput.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    weightInput.getEditor().setText(newValue.replaceAll("[^\\d]", ""));
                }
            });
            weightInput.setHeaderText("Enter new weight: ");
            weightInput.setGraphic(null);
            weightInput.showAndWait();
            String newWeight = weightInput.getEditor().getText();
            viewModel.updateWeight(this, newWeight);
//            System.out.println(this.weight.get());
//            System.out.println(viewModel.getWeights().get(this).get());
        });
        menu.getItems().add(changeWeight);
        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(e -> {
            view.removePrompt();
            for (GraphNode node : viewModel.getConnections().keySet()) {
                node.setFill(Color.WHITE);
            }
            // Remove from display

            pane.getChildren().removeAll(this, this.label);
            // Remove from model
            viewModel.removeEdge(this);
        });
        this.label.addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
            menu.show(pane, event.getScreenX(), event.getScreenY());
            event.consume();
        });
        pane.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            menu.hide();
        });
        menu.getItems().add(delete);
        //endregion
        if (viewModel.getDirected()) {
//            //Creates an arrow tip, first pair is the tip, second pair is left of arrow (assuming tip is top)
//            //Third pair is right of arrow
//            Polygon arrowTip = new Polygon(this.getEndX(), this.getEndY(), 10 * Math.tan(180), -10,
//                    -10 * Math.tan(180), -10);
//            List.of(this.startXProperty(), this.startYProperty(), this.endXProperty(), this.endYProperty()).forEach(p -> p.addListener((obs, oldValue, newValue) -> {
//                ObservableList<Double> points = arrowTip.getPoints();
//                double slope = (this.getEndY() - this.getStartY())/(this.getEndX() - this.getStartX());
//                points.set(0, this.getEndX());
//                points.set(1, this.getEndY());
//                points.set(2, 10 * (Math.cos(Math.atan(slope) + 30)) + this.getEndX());
//                points.set(3, 10 * (Math.sin(Math.atan(slope) - 30)) + this.getEndY());
//                points.set(4, 5 * (Math.cos(Math.atan(slope) - 30)) + this.getEndX());
//                points.set(5, 5 * (Math.sin(Math.atan(slope) - 30)) - this.getEndY())  ;
//                System.out.println(points.toString());
//            }));
//            arrowTip.setFill(Color.GREEN);


            System.out.println(arrowTip.getPoints());
            this.view.getPane().getChildren().add(arrowTip);
        }
        this.viewModel.createNewEdge(this);
        System.out.println(viewModel.getWeights().get(this));
        this.weight.bind(viewModel.getWeights().get(this));

        this.label.textProperty().bind(weight.asString());

    }
    public GraphNode getSource() {
        return source;
    }
    public GraphNode getTarget() {
        return target;
    }

    public IntegerProperty getWeight() {
        return weight;
    }
    public Label getLabel() {
        return this.label;
    }
}
