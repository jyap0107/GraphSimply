package org.graphsimply;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphSimplyModel {

    private ArrayList<GraphNodeData> nodes = new ArrayList<>();
    private ArrayList<GraphEdgeData> edges = new ArrayList<>();
    private Map<String, Map<String, Integer>> adjacencyList = new HashMap<>();

    //region NODES: CREATE, ADD, REMOVE
    // Handles purely the creation of a new node.
    public void createNewNodeData(String name) {
        GraphNodeData newNode = new GraphNodeData(name);
        addNodeToLists(newNode);
    }
    // Handles adding the new node to all the lists.
    public void addNodeToLists(GraphNodeData node) {
        nodes.add(node);
        ArrayList<Integer> newList = new ArrayList<>();
        adjacencyList.put(node.getName(), new HashMap<String, Integer>());
    }
    //Remove node from the master list and then the adjacency list.
    public void removeNodeFromList(String name) {
        GraphNodeData toRemove = null;
        for (GraphNodeData node : nodes) {
            if (node.getName().equals(name)) {
                nodes.remove(node);
                toRemove = node;
                break;
            }
        }
        adjacencyList.remove(name);
        adjacencyList.entrySet().removeIf(entry -> entry.getValue().entrySet().removeIf(entry2 -> entry2.getKey().equals(name)));
    }
    //endregion

    //region EDGES: CREATE, ADD, REMOVE
    // Add edge to adjacency list, also adds edge to the source and target.
    public void createNewEdgeData(int edgeId, GraphNode source, GraphNode target) {
        GraphNodeData src = null;
        GraphNodeData tgt = null;
        for (GraphNodeData node : nodes) {
            if (source.getName().equals(node.getName())) src = node;
            if (target.getName().equals(node.getName())) tgt = node;
        }
        GraphEdgeData newEdge = new GraphEdgeData(edgeId, src, tgt);
        addEdgeToLists(newEdge);
    }
    public void addEdgeToLists(GraphEdgeData edge) {
        edges.add(edge);
        GraphNodeData source = edge.getSource();
        GraphNodeData target = edge.getTarget();
        adjacencyList.get(source.getName()).put(target.getName(), edge.getWeight());
    }
    public void removeEdgeFromList(GraphEdge edge) {
        GraphEdgeData edgeToRemove = null;
        for (GraphEdgeData edgeData : edges) {
            if (edgeData.getEdgeId() == edge.getEdgeId()) edgeToRemove = edgeData;
        }
        GraphNodeData source = edgeToRemove.getSource();
        GraphNodeData target = edgeToRemove.getTarget();
        adjacencyList.get(source.getName()).remove(target.getName());
    }
    //endregion
    public void addEdgeToNode(GraphNode node, GraphNode edge) {
        GraphNodeData nodeToAdd = null;
        GraphNodeData edgeToAdd = null;
        for (GraphNode node : nodes)
    }
    public void updateDFS() {

    }
    public void printLists() {
        System.out.println(adjacencyList.toString());
    }

    public ArrayList<GraphNodeData> getNodes() {
        return this.nodes;
    }
}
