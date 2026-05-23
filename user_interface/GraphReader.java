package etap2_java;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

public class GraphReader {
    private static final int BINARY_RECORD_SIZE = Integer.BYTES + 2 * Double.BYTES;

    // Graph files are text edge lists: <edge_name> <vertex_A> <vertex_B> <weight>.
    public static void loadGraphTxt(File file, Graph graph) throws IOException {
        graph.clear();
        int lineNumber = 0;

        try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s+");
                if (parts.length != 4) {
                    throw new IllegalArgumentException(
                            "Line " + lineNumber + ": expected 4 values: <edge_name> <source> <target> <weight>");
                }

                double weight = parseDouble(parts[3], "edge weight", lineNumber);
                graph.addEdge(parts[0], parts[1], parts[2], weight);
            }
        }

        if (graph.edges.isEmpty()) {
            throw new IllegalArgumentException("Graph file is empty or contains no valid edges.");
        }
    }

    // Coordinate text files use the C-compatible format: <vertex_id> <x> <y>.
    public static void loadCoordsTxt(File file, Graph graph) throws IOException {
        requireLoadedGraph(graph);
        Set<String> loadedVertices = new HashSet<>();
        int lineNumber = 0;

        try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s+");
                if (parts.length != 3) {
                    throw new IllegalArgumentException(
                            "Line " + lineNumber + ": expected 3 values: <vertex_id> <x> <y>");
                }

                applyCoordinate(parts[0],
                        parseDouble(parts[1], "x coordinate", lineNumber),
                        parseDouble(parts[2], "y coordinate", lineNumber),
                        graph,
                        loadedVertices,
                        "Line " + lineNumber);
            }
        }

        requireAllCoordinates(graph, loadedVertices);
    }

    public static void saveCoordsTxt(File file, Graph graph) throws IOException {
        requireLoadedGraph(graph);

        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath())) {
            for (Graph.Node node : graph.nodes.values()) {
                writer.write(node.id + " " + node.x + " " + node.y);
                writer.newLine();
            }
        }
    }

    public static void loadCoordsBinary(File file, Graph graph) throws IOException {
        requireLoadedGraph(graph);
        long size = Files.size(file.toPath());
        if (size % BINARY_RECORD_SIZE != 0) {
            throw new IllegalArgumentException(
                    "Binary coordinate file size must be divisible by " + BINARY_RECORD_SIZE + " bytes.");
        }

        byte[] bytes = Files.readAllBytes(file.toPath());
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.nativeOrder());
        Set<String> loadedVertices = new HashSet<>();
        int recordNumber = 0;

        // This mirrors the existing C binary output and is platform-dependent.
        // Use it for files produced by the same C toolchain/machine; TXT is safer.
        while (buffer.remaining() >= BINARY_RECORD_SIZE) {
            recordNumber++;
            String id = String.valueOf(buffer.getInt());
            double x = buffer.getDouble();
            double y = buffer.getDouble();

            if (!Double.isFinite(x) || !Double.isFinite(y)) {
                throw new IllegalArgumentException(
                        "Binary record " + recordNumber + ": invalid coordinate value.");
            }

            applyCoordinate(id, x, y, graph, loadedVertices, "Binary record " + recordNumber);
        }

        requireAllCoordinates(graph, loadedVertices);
    }

    private static double parseDouble(String value, String label, int lineNumber) {
        try {
            double parsed = Double.parseDouble(value);
            if (!Double.isFinite(parsed)) {
                throw new NumberFormatException();
            }
            return parsed;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    "Line " + lineNumber + ": invalid " + label + " \"" + value + "\".");
        }
    }

    private static void applyCoordinate(String id, double x, double y, Graph graph,
                                        Set<String> loadedVertices, String location) {
        Graph.Node node = graph.nodes.get(id);
        if (node == null) {
            // Current C import creates an extra vertex 0 for 1-based input; ignore only that case.
            if ("0".equals(id) && !graph.nodes.containsKey("0")) {
                return;
            }
            throw new IllegalArgumentException(location + ": unknown vertex \"" + id + "\" in coordinate file.");
        }

        node.x = x;
        node.y = y;
        loadedVertices.add(id);
    }

    private static void requireLoadedGraph(Graph graph) {
        if (graph.nodes.isEmpty()) {
            throw new IllegalArgumentException("Load a graph before loading or saving coordinates.");
        }
    }

    private static void requireAllCoordinates(Graph graph, Set<String> loadedVertices) {
        for (String id : graph.nodes.keySet()) {
            if (!loadedVertices.contains(id)) {
                throw new IllegalArgumentException("Missing coordinates for vertex \"" + id + "\".");
            }
        }
    }
}
