package org.graphsimply;

import java.lang.reflect.Array;
import java.util.*;

public class GraphSimplyModel {

    private Map<String, Map<String, Integer>> adjacencyList = new HashMap<>();


    public void createNewNode(GraphNode node, String name) {
        adjacencyList.put(name, new HashMap<String, Integer>());
        printLists();
    }

    public void removeNodeFromList(String name) {
        adjacencyList.remove(name);
        adjacencyList.entrySet().removeIf(entry -> entry.getValue().entrySet().removeIf(entry2 -> entry2.getKey().equals(name)));
    }

    public void createNewEdge(GraphEdge edge) {
        GraphNode source = edge.getSource();
        GraphNode target = edge.getTarget();
        adjacencyList.get(source.getName().get()).put(target.getName().get(), edge.getWeight().get());
        adjacencyList.get(target.getName().get()).put(source.getName().get(), edge.getWeight().get());
        printLists();
    }
    public void removeEdgeFromList(GraphEdge edge) {
        String source = edge.getSource().getName().get();
        String target = edge.getTarget().getName().get();
        adjacencyList.get(source).remove(target);
        adjacencyList.get(target).remove(source);
    }
    public String updateName(GraphNode node, String newName) {
        String prevName = node.getName().get();
        Map prevMap = adjacencyList.remove(prevName);
        if (prevMap != null) adjacencyList.put(newName, prevMap);
        for (Map.Entry<String, Map<String, Integer>> entry : adjacencyList.entrySet()) {
            System.out.println(entry);
            try {
                int prevValue = entry.getValue().remove(prevName);
                System.out.println("Print prevValue: " + prevValue);
                if (prevValue != 0) entry.getValue().put(newName, prevValue);
            }
            catch (NullPointerException e) {
            }
        }
        return newName;
    }
    public int updateEdge(GraphEdge edge, int newWeight) {
        String source = edge.getSource().getName().get();
        String target = edge.getTarget().getName().get();
        adjacencyList.get(source).put(target, newWeight);
        adjacencyList.get(target).put(source, newWeight);
        return newWeight;
    }
    public void updateDFS() {

    }
    public void printLists() {
       for (Map.Entry<String, Map<String, Integer>> entry : adjacencyList.entrySet()) {
           System.out.print(entry.getKey() + " => ");
           for (Map.Entry<String, Integer> entry2 : entry.getValue().entrySet()) {
               System.out.print("{" +entry2.getValue() + " : " + entry2.getKey() + "} ");
           }
           System.out.println("");
       }
        System.out.println("");
    }
}
