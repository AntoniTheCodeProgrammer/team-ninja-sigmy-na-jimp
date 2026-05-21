package etap2_java;

public class CircularLayout implements LayoutAlgorithm {
    @Override
    public void applyLayout(Graph graph, int width, int height) {
        int n = graph.nodes.size();
        if (n == 0) return;
        
        double radius = Math.min(width, height) / 2.5;
        double centerX = width / 2.0;
        double centerY = height / 2.0;
        
        int i = 0;
        for (Graph.Node node : graph.nodes.values()) {
            double angle = 2 * Math.PI * i / n;
            node.x = centerX + radius * Math.cos(angle);
            node.y = centerY + radius * Math.sin(angle);
            i++;
        }
    }
}