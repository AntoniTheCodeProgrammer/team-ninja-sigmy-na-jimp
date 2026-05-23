package etap2_java;

import java.util.*;

public class Graph {
    public static class Node {
        public String id;
        public double x, y;
        public Node(String id) { this.id = id; }
    }

    public static class Edge {
        public String name;
        public Node source, target;
        public double weight;
        public Edge(String name, Node source, Node target, double weight) {
            this.name = name; this.source = source; this.target = target; this.weight = weight;
        }
    }

    public Map<String, Node> nodes = new LinkedHashMap<>();
    public List<Edge> edges = new ArrayList<>();

    public Node getOrCreateNode(String id) {
        return nodes.computeIfAbsent(id, Node::new);
    }
    
    public void addEdge(String name, String srcId, String tgtId, double weight) {
        Node src = getOrCreateNode(srcId);
        Node tgt = getOrCreateNode(tgtId);
        edges.add(new Edge(name, src, tgt, weight));
    }

    public void clear() {
        nodes.clear();
        edges.clear();
    }
}
