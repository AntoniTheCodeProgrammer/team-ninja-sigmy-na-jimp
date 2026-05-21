import java.util.HashMap;
import java.util.Map;

public class ForceDirectedLayout implements LayoutAlgorithm {

    @Override
    public void applyLayout(Graph graph, int width, int height) {
        if (graph.nodes.isEmpty()) return;

        int iterations = 150;
        double area = width * height;
        // Stała optymalnej odległości między węzłami
        double k = Math.sqrt(area / graph.nodes.size()); 
        // Początkowa 'temperatura' określająca maksymalne przesunięcie w jednym kroku
        double temperature = width / 10.0; 

        // Inicjalizacja wektorów przesunięć (sił)
        Map<Graph.Node, Double> dispX = new HashMap<>();
        Map<Graph.Node, Double> dispY = new HashMap<>();

        // Losowe rozmieszczenie początkowe (jeśli węzły nie mają współrzędnych)
        for (Graph.Node v : graph.nodes.values()) {
            if (v.x == 0 && v.y == 0) {
                v.x = Math.random() * width;
                v.y = Math.random() * height;
            }
        }

        for (int iter = 0; iter < iterations; iter++) {
            
            // 1. Oblicz siły ODPYCHAJĄCE (każdy węzeł odpycha każdy inny)
            for (Graph.Node v : graph.nodes.values()) {
                dispX.put(v, 0.0);
                dispY.put(v, 0.0);
                for (Graph.Node u : graph.nodes.values()) {
                    if (!v.id.equals(u.id)) {
                        double deltaX = v.x - u.x;
                        double deltaY = v.y - u.y;
                        double distance = Math.max(0.01, Math.sqrt(deltaX * deltaX + deltaY * deltaY));
                        
                        // Siła odpychania: fr(x) = k^2 / x
                        double force = (k * k) / distance;
                        
                        dispX.put(v, dispX.get(v) + (deltaX / distance) * force);
                        dispY.put(v, dispY.get(v) + (deltaY / distance) * force);
                    }
                }
            }

            // 2. Oblicz siły PRZYCIĄGAJĄCE (działają tylko między połączonymi węzłami)
            for (Graph.Edge edge : graph.edges) {
                Graph.Node v = edge.source;
                Graph.Node u = edge.target;

                double deltaX = v.x - u.x;
                double deltaY = v.y - u.y;
                double distance = Math.max(0.01, Math.sqrt(deltaX * deltaX + deltaY * deltaY));

                // Siła przyciągania: fa(x) = x^2 / k
                double force = (distance * distance) / k;

                double dispXv = dispX.get(v) - (deltaX / distance) * force;
                double dispYv = dispY.get(v) - (deltaY / distance) * force;
                double dispXu = dispX.get(u) + (deltaX / distance) * force;
                double dispYu = dispY.get(u) + (deltaY / distance) * force;

                dispX.put(v, dispXv);
                dispY.put(v, dispYv);
                dispX.put(u, dispXu);
                dispY.put(u, dispYu);
            }

            // 3. Zaktualizuj pozycje i zastosuj 'temperaturę' do ograniczenia maksymalnego przesunięcia
            for (Graph.Node v : graph.nodes.values()) {
                double dx = dispX.get(v);
                double dy = dispY.get(v);
                double distance = Math.max(0.01, Math.sqrt(dx * dx + dy * dy));

                // Ogranicz przesunięcie do aktualnej temperatury
                v.x += (dx / distance) * Math.min(distance, temperature);
                v.y += (dy / distance) * Math.min(distance, temperature);

                // Zabezpieczenie przed 'wyjściem' poza ekran
                v.x = Math.max(10, Math.min(width - 10, v.x));
                v.y = Math.max(10, Math.min(height - 10, v.y));
            }

            // Obniż temperaturę (chłodzenie) w kolejnych iteracjach
            temperature *= 0.95; 
        }
    }
}
