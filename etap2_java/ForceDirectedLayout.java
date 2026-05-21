package etap2_java;

// Uproszczony algorytm Fruchterman-Reingold
public class ForceDirectedLayout implements LayoutAlgorithm {
    @Override
    public void applyLayout(Graph graph, int width, int height) {
        int iterations = 100;
        double area = width * height;
        double k = Math.sqrt(area / (graph.nodes.size() + 1));
        double temperature = width / 10.0;

        for (int iter = 0; iter < iterations; iter++) {
            // Logika sił odpychających i przyciągających została tutaj maksymalnie uproszczona 
            // Dla rzeczywistego projektu C zaimplementujesz tu pełne wektory sił.
            // Aby graf w Javie wyglądał znośnie na start, zróbmy pseudo-fizykę lub rzucenie losowe 
            // z ułożeniem w obszarze jeśli brak C:
            
            for (Graph.Node v : graph.nodes.values()) {
                if(v.x == 0 && v.y == 0) {
                   v.x = Math.random() * width;
                   v.y = Math.random() * height;
                }
            }
            // W tym miejscu powinna znaleźć się pętla obliczająca dyspersję (odpychanie) 
            // i sprężyny (przyciąganie wg krawędzi).
        }
    }
}
