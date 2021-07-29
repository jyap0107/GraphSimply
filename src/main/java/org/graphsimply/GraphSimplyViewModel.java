package org.graphsimply;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;

public class GraphSimplyViewModel {

    private boolean directed = true;
    private String cursor = "";
    private GraphSimplyModel model;
    private GraphNode source;
    private GraphNode dfsSource;
    // For binding
    private ArrayList<StringProperty> nodeNames = new ArrayList<>();
    private ArrayList<IntegerProperty> weights = new ArrayList<>();
    // Current GUI nodes.
    private ArrayList<GraphNode> nodes = new ArrayList<>();
    private ArrayList<GraphEdge> edges = new ArrayList<>();


    //Properties
    private StringProperty dfsTextProperty = new SimpleStringProperty("");
    private StringProperty bfsTextProperty = new SimpleStringProperty("");

    public GraphSimplyViewModel() {
        this.model = new GraphSimplyModel();
    }
    public GraphSimplyViewModel(GraphSimplyModel model) {
        this.model = model;
    }
    public void toggleDirected() {
        directed = !directed;
    }

    public void toggleDrag() {
        if (cursor.equals("select")) {
            for (GraphNode node : nodes) {
                node.enableDrag();
            }
        }
        else {
            for (GraphNode node : nodes) {
                node.disableDrag();
            }
        }
    }
    public void toggleEdgeCreation() {
        if (cursor.equals("edge")) {
            for (GraphNode node : nodes) {
                node.enableEdgeCreation();
            }
        }
        else {
            for (GraphNode node : nodes) {
                node.disableEdgeCreation();
            }
        }
    }

    public void createNewEdge(GraphEdge edge) {
        edges.add(edge);
        model.createNewEdgeData(edge.getEdgeId(), edge.getSource(), edge.getTarget());

    }
    public void setCursor(String cursor) {
        this.cursor = cursor;
        System.out.println(this.cursor);
    }
    public String getCursor() {
        return cursor;
    }
    public void setSource(GraphNode source) {
        this.source = source;
    }
    public GraphNode getSource() {
        return source;
    }
    public void removeNode(GraphNode node) {
        nodes.remove(node);
        model.removeNodeFromList(node.getName());
    }
    public void removeEdge(GraphEdge edge) {
        model.removeEdgeFromList(edge);
    }
    public StringProperty getDfsTextProperty() {
        return dfsTextProperty;
    }
    public StringProperty getBfsTextProperty() {
        return bfsTextProperty;
    }
    public void setDfsSource(GraphNode dfsSource) {
        this.dfsSource = dfsSource;
    }
    public void createNewNode(GraphNode node) {
        nodes.add(node);
        model.createNewNodeData(node.getName());
    }
    public ArrayList<GraphEdge> getEdges() {
        return edges;
    }
    public void addEdgeToNode(GraphNode node, GraphEdge edge) {
        model.addEdgeToNode(node, edge);
    }
}
