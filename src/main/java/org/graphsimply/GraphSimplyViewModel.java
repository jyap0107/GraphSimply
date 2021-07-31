package org.graphsimply;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.*;

public class GraphSimplyViewModel {

    private boolean directed = true;
    private String cursor = "";
    private GraphSimplyModel model;
    private GraphNode source;
    private GraphNode dfsSource;
    // For binding
    private ArrayList<StringProperty> nodeNames = new ArrayList<>();
    private Map<GraphEdge, IntegerProperty> weights = new HashMap<>();
    // Current GUI nodes.
    private Map<GraphNode, ArrayList<GraphEdge>> connections = new HashMap<>();
    //Properties
    private StringProperty dfsTextProperty = new SimpleStringProperty("");
    private StringProperty bfsTextProperty = new SimpleStringProperty("");

    //TODO
    // Move the name thing to this class, have graphnode be created, then assignn ame afterwards.

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
            for (GraphNode node : connections.keySet()) {
                node.enableDrag();
            }
        }
        else {
            for (GraphNode node : connections.keySet()) {
                node.disableDrag();
            }
        }
    }
    public void toggleEdgeCreation() {
        if (cursor.equals("edge")) {
            for (GraphNode node : connections.keySet()) {
                node.enableEdgeCreation();
            }
        }
        else {
            for (GraphNode node : connections.keySet()) {
                node.disableEdgeCreation();
            }
        }
    }

    public void createNewEdge(GraphEdge edge) {
        connections.get(edge.getSource()).add(edge);
        connections.get(edge.getTarget()).add(edge);
        weights.put(edge, new SimpleIntegerProperty(edge.getWeight().get()));
        model.createNewEdge(edge);
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
        connections.remove(node);
        model.removeNodeFromList(node.getName().get());
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
    public int createNewNode(GraphNode node, String name) {
        connections.put(node, new ArrayList<>());
        model.createNewNode(node, name);
        nodeNames.add(new SimpleStringProperty(name));
        // return the index in which it was added for binding
        return nodeNames.size() - 1;
    }
    public ArrayList<GraphEdge> getEdges() {
        Collection<ArrayList<GraphEdge>> edgeCollection = connections.values();
        ArrayList<GraphEdge> allEdges = new ArrayList<>();
        for (ArrayList<GraphEdge> list : edgeCollection) {
            allEdges.addAll(list);
        }
        return allEdges;
    }
    public ArrayList<GraphEdge> getIncidentEdges(GraphNode node) {
        return connections.get(node);
    }
    public void removeEdgeFromNode(GraphNode node, GraphEdge edge) {
        connections.get(node).remove(edge);
    }
    public void removeEdge(GraphEdge edge) {
        connections.get(edge.getSource()).remove(edge);
        connections.get(edge.getTarget()).remove(edge);
        model.removeEdgeFromList(edge);
    }

    public ArrayList<StringProperty> getNodeNames() {
        return nodeNames;
    }
    public void updateName(GraphNode node, String newName) {
        for (String name : GraphNode.names) {
            if (newName.equals(name)) {
                System.out.println("DUPLICATE NAME");
                return;
            }
        }
        int index = -1;
        for (int i = 0; i < nodeNames.size(); i++) {
            if (nodeNames.get(i).getValue().equals(node.getName().getValue())) {
                index = i;
                break;
            }
        }
        nodeNames.get(index).setValue(model.updateName(node, newName));
    }
    public void updateWeight(GraphEdge edge, int newWeight) {
        weights.get(edge).set(model.updateEdge(edge, newWeight));
    }
    public Map<GraphEdge, IntegerProperty> getWeights() { return weights; }
}
