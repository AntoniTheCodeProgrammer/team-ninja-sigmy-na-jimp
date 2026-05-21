package etap2_java;

import java.io.*;
import java.util.Scanner;

public class GraphReader {
    
    // Wczytuje graf z formatu: <nazwa_krawędzi> <wierzchołek_A> <wierzchołek_B> <waga_krawędzi>
    public static void loadGraphTxt(File file, Graph graph) throws Exception {
        graph.clear();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\s+");
                if (parts.length >= 4) {
                    graph.addEdge(parts[0], parts[1], parts[2], Double.parseDouble(parts[3]));
                }
            }
        }
    }

    // Wczytuje pozycje z formatu: <wierzchołek> <x> <y>
    public static void loadCoordsTxt(File file, Graph graph) throws Exception {
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\s+");
                if (parts.length >= 3) {
                    Graph.Node n = graph.nodes.get(parts[0]);
                    if (n != null) {
                        n.x = Double.parseDouble(parts[1]);
                        n.y = Double.parseDouble(parts[2]);
                    }
                }
            }
        }
    }
}
