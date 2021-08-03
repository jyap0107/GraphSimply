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
    private Text dfsText, bfsText;

    @FXML
    private ToggleButton selectButton, nodeButton, edgeButton;

    private GraphSimplyViewModel viewModel;
    private Text prompt;

    public GraphSimplyController() {
    }

    public void init(GraphSimplyViewModel viewModel) {
        this.viewModel = viewModel;
        // Other bindings.
        dfsText.textProperty().bind(viewModel.getDfsTextProperty());
        bfsText.textProperty().bind(viewModel.getBfsTextProperty());
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

    public void updateDFS() {
        prompt = new Text("Select the Node you want to start the search from");
        centerPane.getChildren().add(prompt);
        prompt.layoutXProperty().bind(centerPane.widthProperty().divide(2).subtract(131.8886719));
        prompt.setLayoutY(25);
        viewModel.setPrevCursor(viewModel.getCursor());
        viewModel.setCursor("dfs");
        viewModel.toggleDrag();
        viewModel.toggleClick();
    }
    public void removePrompt() {
        centerPane.getChildren().remove(prompt);
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
