package org.graphsimply;

import java.lang.reflect.Array;
import java.util.*;

public class GraphSimplyModel {

    private Map<String, Map<String, Integer>> adjacencyList = new HashMap<>();
    private String dfsResult = "";


    public void createNewNode(GraphNode node, String name) {
        System.out.println("New name: " + name);
        adjacencyList.put(name, new HashMap<String, Integer>());
        printLists();
    }

    public void removeNodeFromList(String name) {
        adjacencyList.remove(name);
        printLists();
        Iterator<Map<String, Integer>> iterator = adjacencyList.values().iterator();
        while (iterator.hasNext()) {
            Map<String, Integer> next = iterator.next();
            next.entrySet().removeIf(entry -> entry.getKey().equals(name));
        }
        printLists();
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
        System.out.println("Updated name: " + newName);
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
    public void updateDFS(GraphNode source) {
        String srcName = source.getName().get();
        ArrayList<String> visited = new ArrayList<>();
        dfsHelper(srcName, visited);
//        System.out.println("Source: " + srcName);
//        System.out.println("Destination: " + dstName);
        System.out.println(dfsResult.substring(0, dfsResult.length()-3));
        dfsResult = "";
    }
    public void dfsHelper(String current, ArrayList<String> visited) {
        if (!visited.contains(current)) {
            dfsResult += current + " => ";
        }
        while (visited.size() < adjacencyList.size() && !visited.contains(current)) {
            if (adjacencyList.get(current).keySet().isEmpty()) break;
            for (String adjNode : adjacencyList.get(current).keySet()) {
                System.out.println("Adjacent " + adjNode);
                visited.add(current);
                current = adjNode;
                dfsHelper(current, visited);
            }
        }
    }
    // ALSO BIPARTITE
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
    public void clearModel() {
        adjacencyList = new HashMap<>();
        dfsResult = "";
    }
}
