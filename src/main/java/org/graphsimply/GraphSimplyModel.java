package org.graphsimply;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

public class GraphSimplyModel {

    private boolean directed;
    private Map<String, Map<String, Integer>> adjacencyList = new HashMap<>();
    private String dfsResult = "";
    private String bfsResult = "";
    private boolean connected;
    private boolean isBipartite = true;
    private ArrayList<String> colorVisited = new ArrayList<>();

    // All algos currently assume unweighted and undirected.
    public void createNewNode(GraphNode node, String name) {
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
        // Only add target to source if undirected.
        if (!directed) adjacencyList.get(target.getName().get()).put(source.getName().get(), edge.getWeight().get());
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
            try {
                int prevValue = entry.getValue().remove(prevName);
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
    //region DFS
    public String updateDFS(String srcName) {
        printLists();
        dfsResult = "";
        ArrayList<String> visited = new ArrayList<>();
        dfsHelper(srcName, visited);
        String res = dfsResult.substring(0, dfsResult.length()-3);
        if (!connected && adjacencyList.size() != 1) res += "; Graph is disconnected.";
        dfsResult = "";
        return res;
    }
    public void dfsHelper(String current, ArrayList<String> visited) {
        if (!visited.contains(current)) {
            dfsResult += current + " => ";

        }
        while (visited.size() < adjacencyList.size() && !visited.contains(current)) {
            System.out.println(visited.toString());
            if (adjacencyList.get(current).keySet().isEmpty()) break;
            for (String adjNode : adjacencyList.get(current).keySet()) {
                System.out.println(current);
                if (!visited.contains(current)) visited.add(current);
                current = adjNode;
                dfsHelper(current, visited);
            }
        }
        connected = visited.size() >= adjacencyList.size();
    }
    //endregion
    //region BFS
    public String updateBFS(String srcName) {
        bfsResult = "";
        printLists();
        ArrayList<String> visited = new ArrayList<>();
        Queue<String> toProcess = new LinkedList<>();
        toProcess.offer(srcName);
        while (!toProcess.isEmpty()) {
            String processed = toProcess.poll();
            visited.add(processed);
            bfsResult += processed + " => ";
            Set<String> neighbors = adjacencyList.get(processed).keySet();
            toProcess.addAll(neighbors);
            for (String node : visited) {
                toProcess.remove(node);
            }
        }
        connected = visited.size() >= adjacencyList.size();
        String res = bfsResult.substring(0, bfsResult.length() - 3);
        if (!connected && adjacencyList.size() != 1) res += "; Graph is disconnected.";
        bfsResult = "";
        return res;
    }

    //endregion
    //region Bipartite
    public Map<String, Boolean> checkBipartite() {
        colorVisited.clear();
        isBipartite = true;
        //Create array of keys and array for coloring.
        String[] keys = new String[adjacencyList.size()];
        keys = adjacencyList.keySet().toArray(keys);
        boolean[] coloring = new boolean[keys.length];
        //To return
        Map<String, Boolean> colorMatches = new HashMap<>();
        coloring = bipartiteHelper(true, coloring, keys[0], colorVisited, keys);
        // TO COLOR DISCONNECTED; COULD BE CLEANER
        for (String key : adjacencyList.keySet()) {
            if (!colorVisited.contains(key)) {
                coloring = bipartiteHelper(true, coloring, key, colorVisited, keys);
                for (int i = 0; i < keys.length; i++) {
                    colorMatches.put(keys[i], coloring[i]);
                }
            }
        }
        for (int i = 0; i < keys.length; i++) {
            colorMatches.put(keys[i], coloring[i]);
        }
        return colorMatches;
    }
    // This uses an altered form of DFS.
    public boolean[] bipartiteHelper(boolean color, boolean[] coloring, String current, ArrayList<String> visited,
                                     String[] keys) {
        int indexOfCurrent = -1;
        if (!visited.contains(current)) {
            for (int i = 0; i < keys.length; i++) {
                if (keys[i].equals(current)) {
                    coloring[i] = color;
                    indexOfCurrent = i;
                }
            }
        }
        while (visited.size() < adjacencyList.size() && !visited.contains(current)) {
            if (adjacencyList.get(current).keySet().isEmpty()) break;
            for (String adjNode : adjacencyList.get(current).keySet()) {
                // Add if not visited
                if (!visited.contains(current)) {
                    visited.add(current);
                }
                // If visited already, check against current key value.
                if (visited.contains(current) && visited.contains(adjNode)) {
                    int indexOfNeighbor = -1;
                    for (int i = 0; i < keys.length; i++) {
                        if (keys[i].equals(adjNode))  indexOfNeighbor = i;
                    }
                    if (coloring[indexOfCurrent] == coloring[indexOfNeighbor]) isBipartite = false;
                }
                current = adjNode;
                coloring = bipartiteHelper(!color, coloring, current, visited, keys);
            }
        }
        System.out.println(visited.toString());
        return coloring;
    }
    //endregion
    public String updateEulerianPath() {
        int numOddVertices = 0;
        String oddVertexName = "";
        String anyVertexName = "";
        HashMap<String, Integer> numConnections = new HashMap<>();
        for (Map.Entry<String, Map<String, Integer>> entry : adjacencyList.entrySet()) {
            numConnections.put(entry.getKey(), entry.getValue().size());
        }
        for (Map.Entry<String, Integer> entry : numConnections.entrySet()) {
            if (entry.getValue() % 2 != 0) {
                numOddVertices++;
                oddVertexName = entry.getKey();
            }
            anyVertexName = entry.getKey();
        }
        //Check if connected
        updateBFS(anyVertexName);
        if ((numOddVertices == 0 || numOddVertices == 2) && connected) {
            System.out.println("Run thru alg");
            String res = "";
            HashMap<String, Map<String, Integer>> map = copyAdjacencyList();
            //Start at the oddVertexName for HierHolzer's algorithm.
            if (numOddVertices == 2) anyVertexName = oddVertexName;
            Stack<String> tempPath = new Stack<>();
            Stack<String> finalPath = new Stack<>();
            String current = anyVertexName;
            System.out.println("Name: " + current);
            tempPath.push(current);
            System.out.println("Current: ");
            System.out.println(current);
            while (!tempPath.isEmpty()) {
                if (map.get(current).isEmpty()) {
                    finalPath.push(tempPath.pop());
                    System.out.println("So far");
                    System.out.println(finalPath.toString());
                }
                else {
                    for (Map.Entry<String, Integer> entry : map.get(current).entrySet()) {
                        tempPath.push(entry.getKey());
                        String neighbor = entry.getKey();
                        map.get(current).remove(neighbor);
                        map.get(neighbor).remove(current);
                        current = neighbor;
                        break;
                    }
                }
            }
            res = finalPath.toString();
            int nodesVisited = 0;
            while (!finalPath.isEmpty()) {
                finalPath.pop();
                nodesVisited++;
            }
            if (nodesVisited < adjacencyList.size()) return "No Eulerian Path exists.";
            res = res.replaceAll("(?i)\\s*(?:, )s?", " => ");
            System.out.println(res.substring(1, res.length()-1));
            return res.substring(1, res.length() - 1);

        }
        return "No Eulerian Path exists.";
    }

    public String updateShortestPaths(String srcName) {
        //Initialize
        int size = adjacencyList.size();
        Set<Map.Entry<String, Map<String, Integer>>> entrySet = adjacencyList.entrySet();
        // Vertices, dist, and prev all have matching indices.
        ArrayList<String> vertices = new ArrayList<>(adjacencyList.keySet());
        Set<String> unvisited = new HashSet<>();
        int[] dist = new int[size];
        String[] prev = new String[size];
        int i = 0;
        for (Map.Entry<String, Map<String, Integer>> entry : entrySet) {
            dist[i] = Integer.MAX_VALUE;
            prev[i] = "";
            unvisited.add(entry.getKey());
            i++;
        }
        dist[vertices.indexOf(srcName)] = 0;
        // Loop
        while (!unvisited.isEmpty()) {
            // Find unvisited vertex with minimum distance to source
            String curr = "";
            int min = Integer.MAX_VALUE;
            for (String vertex : unvisited) {
                int index = vertices.indexOf(vertex);
                if (dist[index] < min) {
                    min = dist[index];
                    curr = vertex;
                }
            }
            unvisited.remove(curr);
            if (curr.equals("")) {
                break;
            }
            for (Map.Entry<String, Integer> connection : adjacencyList.get(curr).entrySet()) {
                // If already visited, skip
                String key = connection.getKey();
                if (!unvisited.contains(key)) continue;
                int alt = dist[vertices.indexOf(curr)] + connection.getValue();
                if (alt < dist[vertices.indexOf(key)]) {
                    dist[vertices.indexOf(key)] = alt;
                    prev[vertices.indexOf(key)] = curr;
                }
            }
        }
        // Reconstruct the path
        // Each iteration makes a new path.
//        prev[vertices.indexOf(srcName)] = srcName;
        String[] pathStrings = new String[dist.length];
        int index = 0;
        for (String vertex : vertices) {
            if (vertex.equals(srcName)) continue;
            if (unvisited.contains(vertex)) continue;
            String res = "";
            String temp = vertex;
            // Helps complete the path.
            while (!temp.equals(srcName)) {
                res = " => " + temp + res;
                temp = prev[vertices.indexOf(temp)];
            }
            res = srcName + res + " : " + dist[vertices.indexOf(vertex)];
            pathStrings[index] = res;
            index++;
        }
        String result = "";
        for (String path : pathStrings) {
            if (path == null) continue;
            result += path + "\n";
        }
        System.out.println(result);
        return result;
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
    public void clearModel() {
        adjacencyList.clear();
        dfsResult = "";
        bfsResult = "";
        connected = false;
        colorVisited.clear();
        isBipartite = false;
    }
    public int getNumNodes() {
        return adjacencyList.size();
    }
    public boolean getBipartite() {
        return this.isBipartite;
    }
    public HashMap<String, Map<String, Integer>> copyAdjacencyList() {
        HashMap<String, Map<String, Integer>> copy = new HashMap<>();
        for (Map.Entry<String, Map<String, Integer>> entry : adjacencyList.entrySet()) {
            HashMap<String, Integer> innerMap = new HashMap<>();
            for (Map.Entry<String, Integer> entry2 : entry.getValue().entrySet()) {
                innerMap.put(new String(entry2.getKey()), (entry2.getValue()));
            }
            copy.put(new String(entry.getKey()), innerMap);
        }
        return copy;
    }
    public void setDirected(boolean flag) {
        directed = flag;
    }
}
