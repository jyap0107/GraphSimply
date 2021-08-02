package org.graphsimply;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.shape.Shape;


public class GraphEdge extends Line {
    private GraphNode source;
    private GraphNode target;
    private GraphSimplyController view;
    private GraphSimplyViewModel viewModel;
    private IntegerProperty weight = new SimpleIntegerProperty(100);
    private Label label;
    private IntegerProperty index;

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
        MenuItem changeWeight = new MenuItem("Change weight");
        Pane pane = this.view.getPane();
        changeWeight.setOnAction(e -> {
            System.out.println("Rename");
        });
        changeWeight.setOnAction(e -> {
            TextInputDialog weightInput = new TextInputDialog(this.weight.get() +"");
            weightInput.setHeaderText("Enter new weight: ");
            weightInput.setGraphic(null);
            weightInput.showAndWait();
            int newWeight = Integer.parseInt(weightInput.getEditor().getText());
            viewModel.updateWeight(this, newWeight);
//            System.out.println(this.weight.get());
//            System.out.println(viewModel.getWeights().get(this).get());
        });
        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(e -> {
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
        menu.getItems().addAll(changeWeight, delete);
        //endregion
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
