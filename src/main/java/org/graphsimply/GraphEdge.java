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
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

import java.time.Year;
import java.util.List;


public class GraphEdge extends Line {
    private GraphNode source;
    private GraphNode target;
    private GraphSimplyController view;
    private GraphSimplyViewModel viewModel;
    private IntegerProperty weight = new SimpleIntegerProperty(0);
    private Label label;
    private boolean isSecondEdge;
    private IntegerProperty index;
    private Polygon arrowTip;

    public GraphEdge() {


    }
    public GraphEdge(GraphNode source, GraphNode target, GraphSimplyViewModel viewModel, GraphSimplyController view,
                     boolean isSecondEdge) {
        this.source = source;
        this.target = target;
        this.view = view;
        this.viewModel = viewModel;
        this.setFill(Color.BLACK);
        this.label = new Label(weight.get() + "");
        this.isSecondEdge = isSecondEdge;

        if (isSecondEdge) {
            doubleEdgeBinding(false);
        }
        else {
            singleEdgeBinding();
        }
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
            //If the source contains an edge whose target is the source and is a second edge, turn it into a first
            // edge and reset bindings.
            for (GraphNode node : viewModel.getConnections().keySet()) {
                node.setFill(Color.WHITE);
            }
            // Remove from display

            pane.getChildren().removeAll(this, this.label, this.arrowTip);
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
        //region arrowTip
        if (viewModel.getDirected()) {
            //Creates an arrow tip, first pair is the tip, second pair is left of arrow (assuming tip is top)
            //Third pair is right of arrow
            this.arrowTip = new Polygon(0, 0, 0, 0, 0, 0);
            setArrowTipPoints();
            List.of(this.startXProperty(), this.startYProperty(), this.endXProperty(), this.endYProperty()).forEach(p -> p.addListener((obs, oldValue, newValue) -> {
                setArrowTipPoints();
            }));
            arrowTip.setFill(Color.BLACK);
            this.view.getPane().getChildren().add(arrowTip);
            this.arrowTip.toBack();
        }
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
    public Polygon getArrowTip() {
        return this.arrowTip;
    }
    public void setArrowTipPoints() {
        ObservableList<Double> points = arrowTip.getPoints();
        // Uses pythagorean theorem and 30-60-90 properties to properly format the arrow tip.
        double startX = this.getStartX();
        double startY = this.getStartY();
        double endX = this.getEndX();
        double endY = this.getEndY();
        double slope = (endY - startY)/(endX - startX);
        int side = 12;
        double xIntersect = Math.sqrt((3.0/4.0 * Math.pow(side, 2))/(Math.pow(slope, 2) + 1));
        if (endX > startX) xIntersect = -xIntersect;
        xIntersect += endX;
        double yIntersect = slope * (xIntersect - endX) + endY;
        // Offset values
        double a = Math.sqrt((Math.pow(side, 2)/4)/(1 + 1/(Math.pow(slope, 2))));
        points.set(0, endX);
        points.set(1, endY);
        points.set(2, a+xIntersect);
        points.set(3, -a/slope + yIntersect);
        points.set(4, -a + xIntersect);
        points.set(5, a/slope+ yIntersect);
    }
    public void singleEdgeBinding() {
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
    }
    public void doubleEdgeBinding(boolean counterClockwise) {
        if (counterClockwise) {;}
        else {;}
    }
    public void setIsSecondEdge(boolean flag) {
        isSecondEdge = flag;
    }
    public boolean getIsSecondEdge() {
        return isSecondEdge;
    }
}
