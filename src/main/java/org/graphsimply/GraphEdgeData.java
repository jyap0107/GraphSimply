package org.graphsimply;

public class GraphEdgeData {

    private int weight = 0;
    private int edgeId;
    private GraphNodeData source;
    private GraphNodeData target;

    public GraphEdgeData(int edgeId, GraphNodeData source, GraphNodeData target) {
        this.edgeId = edgeId;
        this.source = source;
        this.target = target;
    }

    public GraphNodeData getSource() {
        return source;
    }
    public GraphNodeData getTarget() {
        return target;
    }
    public int getWeight() {
        return weight;
    }

    public int getEdgeId() {
        return edgeId;
    }
}
