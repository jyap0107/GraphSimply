package org.graphsimply;

import java.util.ArrayList;

public class GraphNodeData {

    private String name;
    private ArrayList<GraphEdgeData> edgeDataList = new ArrayList<>();

    public GraphNodeData(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}
