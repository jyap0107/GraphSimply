package org.graphsimply;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.lang.reflect.Array;
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
    private String[] defaultNames = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
            "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    //TODO
    /* After graph node is created,

    */

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
        nodeNames.remove(node.getName().get());
        model.removeNodeFromList(node.getName().get());
        //Let default name work again
        String name = node.getName().get();
        if (name.length() == 1) {
            int ascii = (int) name.charAt(0);
            defaultNames[ascii - 65] = name;
        }
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
    public void removeEdgesFromNode(GraphNode node, ArrayList<GraphEdge> edges) {
        for (Iterator<GraphEdge> iterator = edges.iterator(); iterator.hasNext();) {
            GraphEdge edge = iterator.next();
            connections.get(node).remove(edge);
            weights.remove(edge);
        }
    }
    public void removeEdge(GraphEdge edge) {
        connections.get(edge.getSource()).remove(edge);
        connections.get(edge.getTarget()).remove(edge);
        weights.remove(edge);
        model.removeEdgeFromList(edge);
    }

    public ArrayList<StringProperty> getNodeNames() {
        return nodeNames;
    }
    public boolean updateName(GraphNode node, String newName) {
        // Check if available
        if (nodeNames.contains(newName)) {
            return false;
        }
        boolean oneLetterName = false;
        if (newName.length() == 1) {
            oneLetterName = true;
            int ascii = (int) newName.charAt(0);
            if (defaultNames[ascii - 65].equals("/")) {
                return false;
            }
        }
        String oldName = node.getName().get();
        //Find within nodeNames what to set.
        int index = -1;
        for (int i = 0; i < nodeNames.size(); i++) {
            if (nodeNames.get(i).getValue().equals(node.getName().getValue())) {
                index = i;
                break;
            }
        }
        nodeNames.get(index).setValue(model.updateName(node, newName));
        //Reset defaultNames
        if (oneLetterName) {
            int newAscii = newName.charAt(0);
            int oldAscii = oldName.charAt(0);
            // The old name in defaultNames is reassigned, the newName is set to "/"
            defaultNames[newAscii - 65] = "/";
            defaultNames[oldAscii - 65] = Character.toString((char)oldAscii);
        }
        return true;
    }
    public void updateWeight(GraphEdge edge, int newWeight) {
        weights.get(edge).set(model.updateEdge(edge, newWeight));
    }
    public Map<GraphEdge, IntegerProperty> getWeights() { return weights; }
    public String[] getDefaultNames() {
        return defaultNames;
    }
    public String assignName() {
//        System.out.println(Arrays.toString(defaultNames));
        for (int i = 0; i < defaultNames.length; i++) {
            if (!defaultNames[i].equals("/")) {
                String temp = defaultNames[i];
                defaultNames[i] = "/";
                return temp;
            }
        }
        // Should never be reached.
        return "Error";
    }
}
