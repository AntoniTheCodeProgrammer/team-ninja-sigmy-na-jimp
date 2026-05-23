package etap2_java;

import java.awt.*;
import java.io.File;
import javax.swing.*;

public class GraphApp extends JFrame {
    private Graph graph = new Graph();
    private GraphPanel graphPanel;

    public GraphApp() {
        setTitle("Wizualizacja Grafu - Projekt Etap 2");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        graphPanel = new GraphPanel(graph);
        add(graphPanel, BorderLayout.CENTER);

        setupMenu();
        setupToolbar();
    }

    private void setupMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Plik");

        JMenuItem loadGraphItem = new JMenuItem("Wczytaj graf (TXT)");
        loadGraphItem.addActionListener(e -> loadFile(true));

        JMenuItem loadCoordsItem = new JMenuItem("Wczytaj współrzędne (TXT)");
        loadCoordsItem.addActionListener(e -> loadFile(false));

        fileMenu.add(loadGraphItem);
        fileMenu.add(loadCoordsItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }

    private void setupToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Wybór algorytmu
        String[] algorithms = {"Circular Layout", "Force Directed"};
        JComboBox<String> algoBox = new JComboBox<>(algorithms);
        JButton applyBtn = new JButton("Zastosuj Algorytm");
        applyBtn.addActionListener(e -> {
            LayoutAlgorithm algo = algoBox.getSelectedIndex() == 0 ?  new CircularLayout() : new ForceDirectedLayout();
            algo.applyLayout(graph, graphPanel.getWidth(), graphPanel.getHeight());
            graphPanel.repaint();
        });

        // Opcje wyświetlania
        JCheckBox labelsCheck = new JCheckBox("Pokaż etykiety", true);
        labelsCheck.addActionListener(e -> {
            graphPanel.showLabels = labelsCheck.isSelected();
            graphPanel.repaint();
        });

        JCheckBox weightsCheck = new JCheckBox("Pokaż wagi", false);
        weightsCheck.addActionListener(e -> {
            graphPanel.showWeights = weightsCheck.isSelected();
            graphPanel.repaint();
        });

        toolbar.add(new JLabel("Algorytm: "));
        toolbar.add(algoBox);
        toolbar.add(applyBtn);
        toolbar.add(labelsCheck);
        toolbar.add(weightsCheck);

        add(toolbar, BorderLayout.NORTH);
    }

    private void loadFile(boolean isGraph) {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                if (isGraph) {
                    GraphReader.loadGraphTxt(file, graph);
                    // Domyślne rozmieszczenie po wczytaniu
                    new CircularLayout().applyLayout(graph, graphPanel.getWidth(), graphPanel.getHeight());
                } else {
                    GraphReader.loadCoordsTxt(file, graph);
                }
                graphPanel.repaint();
                JOptionPane.showMessageDialog(this, "Wczytano węzłów: " + graph.nodes.size() + ", krawędzi: " + graph.edges.size());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Błąd wczytywania: " + ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}