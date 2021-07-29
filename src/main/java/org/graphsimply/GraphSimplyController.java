package org.graphsimply;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.*;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class GraphSimplyController implements Initializable {
    @FXML
    private RadioButton directedButton, undirectedButton;

    @FXML
    private Pane centerPane;

    @FXML
    private Button dfsButton, bfsButton;

    @FXML
    private Text dfsText, bfsText;

    private float sample = 0;

    private GraphSimplyViewModel viewModel;

    public GraphSimplyController() {
    }

    public void init(GraphSimplyViewModel viewModel) {
        this.viewModel = viewModel;
        // Other bindings.
        dfsText.textProperty().bind(viewModel.getDfsTextProperty());
        bfsText.textProperty().bind(viewModel.getBfsTextProperty());
    }
    public void toggleDirected() {
        viewModel.toggleDirected();
    }
    public void setCursorToSelect() {
        viewModel.setCursor("select");
        viewModel.toggleDrag();
        viewModel.toggleEdgeCreation();
    }
    public void setCursorToNode() {
        viewModel.setCursor("node");
        viewModel.toggleDrag();
        viewModel.toggleEdgeCreation();
    }
    public void setCursorToEdge() {
        viewModel.setCursor("edge");
        viewModel.toggleDrag();
        viewModel.toggleEdgeCreation();
    }
    public void createNode(MouseEvent e) {
        if (viewModel.getCursor().equals("node") && e.getButton() == MouseButton.PRIMARY) {
            GraphNode node = new GraphNode(e.getX(), e.getY(), "N/A",
                    this.viewModel, this);
            centerPane.getChildren().addAll(node, node.getLabel());
            viewModel.addNode(node);
        }
    }
    public Pane getPane() {
        return centerPane;
    }

    public void updateDFS() {
        Text dfsPrompt = new Text("Select the Node you want to start the search from");
        centerPane.getChildren().add(dfsPrompt);
        dfsPrompt.layoutXProperty().bind(centerPane.widthProperty().divide(2).subtract(131.8886719));
        dfsPrompt.setLayoutY(25);
        viewModel.setCursor("dfs");

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Rectangle clip = new Rectangle(centerPane.getWidth(),
                centerPane.getHeight());
        clip.heightProperty().bind(centerPane.heightProperty());
        clip.widthProperty().bind(centerPane.widthProperty());
        centerPane.setClip(clip);

    }
}
