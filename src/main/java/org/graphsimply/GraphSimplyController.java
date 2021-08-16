package org.graphsimply;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
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
        boolean isDirected = viewModel.getDirected();
        if (isDirected) {
            toggleConfirmation.setHeaderText("Are you sure you want to switch to an undirected graph?");
        }
        else {
            toggleConfirmation.setHeaderText("Are you sure you want to switch to a directed graph?");
        }
        toggleConfirmation.setContentText("Your previous graph will be cleared.");
        Optional<ButtonType> result = toggleConfirmation.showAndWait();
        if (result.get() == ButtonType.OK) {
            init(new GraphSimplyViewModel());
            viewModel.setDirected(!isDirected);
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
        removePrompt();
        viewModel.setCursor("select");
        viewModel.toggleDrag();
        viewModel.toggleClick();
    }
    public void setCursorToNode() {
        removePrompt();
        viewModel.setCursor("node");
        viewModel.toggleDrag();
        viewModel.toggleClick();
    }
    public void setCursorToEdge() {
        removePrompt();
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
        removePrompt();
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
        removePrompt();
        viewModel.getEulerianPath();
    }
    public void showShortestPaths() {
        removePrompt();
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
    public void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("About GraphSimply");
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.setContentText("GraphSimply lets you easily simulate graphs and graphing algorithms with an easy to use" +
                " UI.");
        alert.showAndWait();
    }
    public void showInstructions() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Instructions");
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.setHeaderText(
                "Drawing Nodes: Click \"Node\" to enable node creation by clicking the diagram space." +
                "\nDrawing Edges: Click \"Edge\" to enable edge creation by clicking two nodes. " +
                "\nMoving Nodes: Click \"Select\" to enable node movement." +
                "\nRemoving Nodes: Right click a node to enable removal." +
                "\nRemoving Edges: Right click an edge to enable removal." +
                "\nRenaming a node: Right click a node to enable renaming." +
                "\nChanging an edge weight: Right click a node to change edge weight." +
                "\nUpdating graph information: Click a button under \"Diagram Actions\" to update the diagram " +
                "accordingly." +
                "\nTo simulate an unweighted graph, keep all node weights as 0.");
        alert.showAndWait();

    }
    public void escapePrompt(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ESCAPE)) {
            System.out.println("yes");
            viewModel.setCursor(viewModel.getPrevCursor());
            viewModel.toggleDrag();
            viewModel.toggleClick();
            removePrompt();
        }
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
