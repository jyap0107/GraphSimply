package org.graphsimply;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.*;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class GraphSimplyController implements Initializable {
    @FXML
    private RadioButton directedButton, undirectedButton;

    @FXML
    private Pane centerPane;

    @FXML
    private Button dfsButton, bfsButton;

    @FXML
    private Text dfsText, bfsText, eulerianPathText, shortestPathsText;

    @FXML
    private ToggleButton selectButton, nodeButton, edgeButton;

    private GraphSimplyViewModel viewModel;
    private Text prompt;

    @FXML
    private Label dfsLabel, bfsLabel, shortestPathsLabel;


    public GraphSimplyController() {
    }

    public void init(GraphSimplyViewModel viewModel) {
        this.viewModel = viewModel;
        // Other bindings.
        dfsText.textProperty().bindBidirectional(viewModel.getDfsTextProperty());
        bfsText.textProperty().bindBidirectional(viewModel.getBfsTextProperty());
        dfsLabel.textProperty().bindBidirectional(viewModel.getDfsLabelProperty());
        bfsLabel.textProperty().bindBidirectional(viewModel.getBfsLabelProperty());

        eulerianPathText.textProperty().bindBidirectional(viewModel.getEulerianPathText());
        shortestPathsText.textProperty().bindBidirectional(viewModel.getShortestPathsTextProperty());
        shortestPathsLabel.textProperty().bindBidirectional(viewModel.getShortestPathsLabelProperty());
    }
    public void toggleDirected() {

        Alert toggleConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
        toggleConfirmation.setTitle("Direction Confirmation");
        String text = viewModel.getDirected() ? "directed" : "undirected";
        toggleConfirmation.setHeaderText("Are you sure you want to change to a " + text + " graph?" );
        toggleConfirmation.setContentText("Your previous graph will be cleared.");
        Optional<ButtonType> result = toggleConfirmation.showAndWait();
        if (result.get() == ButtonType.OK) {
            viewModel.toggleDirected();
            centerPane.getChildren().clear();
            selectButton.setSelected(false);
            nodeButton.setSelected(false);
            edgeButton.setSelected(false);

        }
        if (viewModel.getDirected()) {
            directedButton.setSelected(true);
        }
        else {
            undirectedButton.setSelected(true);
        }
    }
    public void setCursorToSelect() {
        viewModel.setCursor("select");
        viewModel.toggleDrag();
        viewModel.toggleClick();
    }
    public void setCursorToNode() {
        viewModel.setCursor("node");
        viewModel.toggleDrag();
        viewModel.toggleClick();
    }
    public void setCursorToEdge() {
        viewModel.setCursor("edge");
        viewModel.toggleDrag();
        viewModel.toggleClick();
    }
    public void createNode(MouseEvent e) {
        if (viewModel.getCursor().equals("node") && e.getButton() == MouseButton.PRIMARY) {
            if (viewModel.getNodeNames().size() >= 26) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Error");
                alert.setContentText("Max node count: 26");
                alert.showAndWait();
                return;
            }
            GraphNode node = new GraphNode(e.getX(), e.getY(),
                    this.viewModel, this);
            centerPane.getChildren().addAll(node, node.getLabel());
        }
    }
    public Pane getPane() {
        return centerPane;
    }

    public void showDFS() {
        if (viewModel.getNumNodes() == 0) {
            dfsText.setText("No nodes in graph");
            dfsLabel.setText("DFS from Node");
            return;
        }
        prompt = new Text("Select the Node you want to start the search from");
        centerPane.getChildren().add(prompt);
        prompt.layoutXProperty().bind(centerPane.widthProperty().divide(2).subtract(131.8886719));
        prompt.setLayoutY(25);
        viewModel.setPrevCursor(viewModel.getCursor());
        viewModel.setCursor("dfs");
        viewModel.toggleDrag();
        viewModel.toggleClick();
    }
    public void showBFS() {
        if (viewModel.getNumNodes() == 0) {
            bfsText.setText("No nodes in graph");
            bfsLabel.setText("BFS from Node");
            return;
        }
        prompt = new Text("Select the Node you want to start the search from");
        centerPane.getChildren().add(prompt);
        prompt.layoutXProperty().bind(centerPane.widthProperty().divide(2).subtract(131.8886719));
        prompt.setLayoutY(25);
        viewModel.setPrevCursor(viewModel.getCursor());
        viewModel.setCursor("bfs");
        viewModel.toggleDrag();
        viewModel.toggleClick();
    }
    public void removePrompt() {
        centerPane.getChildren().remove(prompt);
    }
    public void showBipartite() {
        removePrompt();
        boolean isBipartite = viewModel.colorGraph();
        if (isBipartite) prompt = new Text("The graph is Bipartite");
        else prompt = new Text("The graph is NOT Bipartite");
        centerPane.getChildren().add(prompt);
        prompt.layoutXProperty().bind(centerPane.widthProperty().divide(2).subtract(131.8886719));
        prompt.setLayoutY(25);
        if (!viewModel.getIsColored()) removePrompt();
    }
    public void showEulerianPath() {
        viewModel.getEulerianPath();
    }
    public void showShortestPaths() {
        if (viewModel.getNumNodes() == 0) {
            shortestPathsText.setText("No nodes in graph");
            shortestPathsLabel.setText("Shortest paths from Node");
            return;
        }
        prompt = new Text("Select the Node you want to start the search from");
        centerPane.getChildren().add(prompt);
        prompt.layoutXProperty().bind(centerPane.widthProperty().divide(2).subtract(131.8886719));
        prompt.setLayoutY(25);
        viewModel.setPrevCursor(viewModel.getCursor());
        viewModel.setCursor("shortestPaths");
        viewModel.toggleDrag();
        viewModel.toggleClick();
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
