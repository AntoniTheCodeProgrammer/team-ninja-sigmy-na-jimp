package etap2_java;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class GraphApp extends JFrame {
    private final Graph graph = new Graph();
    private final GraphPanel graphPanel;
    private final JLabel statusLabel = new JLabel("Load a graph TXT file to begin.");
    private File currentGraphFile;
    private String coordinateStatus = "No coordinates loaded";
    private String algorithmStatus = "No algorithm run";

    public GraphApp() {
        setTitle("Graph Visualization - Java Viewer");
        setSize(960, 680);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        graphPanel = new GraphPanel(graph);
        add(graphPanel, BorderLayout.CENTER);

        setupMenu();
        setupToolbar();
        setupStatusBar();
    }

    private void setupMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu layoutMenu = new JMenu("Layout");
        JMenu viewMenu = new JMenu("View");

        JMenuItem loadGraphItem = new JMenuItem("Load graph (TXT)");
        loadGraphItem.addActionListener(e -> loadGraphTxt());
        JMenuItem loadCoordsTxtItem = new JMenuItem("Load coordinates (TXT)");
        loadCoordsTxtItem.addActionListener(e -> loadCoordsTxt());
        JMenuItem loadCoordsBinItem = new JMenuItem("Load coordinates (BIN)");
        loadCoordsBinItem.addActionListener(e -> loadCoordsBinary());
        JMenuItem saveCoordsItem = new JMenuItem("Save coordinates (TXT)");
        saveCoordsItem.addActionListener(e -> saveCoordsTxt());
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> dispose());

        fileMenu.add(loadGraphItem);
        fileMenu.add(loadCoordsTxtItem);
        fileMenu.add(loadCoordsBinItem);
        fileMenu.add(saveCoordsItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenuItem forceItem = new JMenuItem("Force-directed layout");
        forceItem.addActionListener(e -> runCLayout(1, "Force-directed layout"));
        JMenuItem tutteItem = new JMenuItem("Barycentric Tutte-style Layout");
        tutteItem.addActionListener(e -> runCLayout(2, "Barycentric Tutte-style Layout"));
        layoutMenu.add(forceItem);
        layoutMenu.add(tutteItem);

        JMenuItem resetViewItem = new JMenuItem("Reset view");
        resetViewItem.addActionListener(e -> graphPanel.resetView());
        JCheckBoxMenuItem labelsItem = new JCheckBoxMenuItem("Show labels", true);
        labelsItem.addActionListener(e -> graphPanel.setShowLabels(labelsItem.isSelected()));
        JCheckBoxMenuItem weightsItem = new JCheckBoxMenuItem("Show weights", false);
        weightsItem.addActionListener(e -> graphPanel.setShowWeights(weightsItem.isSelected()));
        viewMenu.add(resetViewItem);
        viewMenu.add(labelsItem);
        viewMenu.add(weightsItem);

        menuBar.add(fileMenu);
        menuBar.add(layoutMenu);
        menuBar.add(viewMenu);
        setJMenuBar(menuBar);
    }

    private void setupToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        toolbar.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));

        JButton loadGraphButton = new JButton("Load graph");
        loadGraphButton.addActionListener(e -> loadGraphTxt());
        JButton loadTxtButton = new JButton("Load coordinates TXT");
        loadTxtButton.addActionListener(e -> loadCoordsTxt());
        JButton loadBinButton = new JButton("Load coordinates BIN");
        loadBinButton.addActionListener(e -> loadCoordsBinary());
        JButton forceButton = new JButton("Force-directed");
        forceButton.addActionListener(e -> runCLayout(1, "Force-directed layout"));
        JButton tutteButton = new JButton("Barycentric Tutte");
        tutteButton.addActionListener(e -> runCLayout(2, "Barycentric Tutte-style Layout"));
        JButton saveButton = new JButton("Save coordinates");
        saveButton.addActionListener(e -> saveCoordsTxt());
        JButton resetButton = new JButton("Reset view");
        resetButton.addActionListener(e -> graphPanel.resetView());

        JCheckBox labelsCheck = new JCheckBox("Labels", true);
        labelsCheck.addActionListener(e -> graphPanel.setShowLabels(labelsCheck.isSelected()));
        JCheckBox weightsCheck = new JCheckBox("Weights", false);
        weightsCheck.addActionListener(e -> graphPanel.setShowWeights(weightsCheck.isSelected()));

        toolbar.add(loadGraphButton);
        toolbar.add(loadTxtButton);
        toolbar.add(loadBinButton);
        toolbar.add(new JSeparator(JSeparator.VERTICAL));
        toolbar.add(forceButton);
        toolbar.add(tutteButton);
        toolbar.add(new JSeparator(JSeparator.VERTICAL));
        toolbar.add(saveButton);
        toolbar.add(resetButton);
        toolbar.add(labelsCheck);
        toolbar.add(weightsCheck);

        add(toolbar, BorderLayout.NORTH);
    }

    private void setupStatusBar() {
        statusLabel.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        add(statusLabel, BorderLayout.SOUTH);
    }

    private void loadGraphTxt() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        try {
            GraphReader.loadGraphTxt(file, graph);
            currentGraphFile = file;
            coordinateStatus = "Coordinates not loaded";
            algorithmStatus = "No algorithm run";
            graphPanel.repaint();
            updateStatus("Loaded graph TXT.");
        } catch (IOException ex) {
            showError("File error", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            showError("Invalid graph file", ex.getMessage());
        }
    }

    private void loadCoordsTxt() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        try {
            GraphReader.loadCoordsTxt(chooser.getSelectedFile(), graph);
            coordinateStatus = "TXT coordinates loaded";
            graphPanel.repaint();
            updateStatus("Loaded coordinate TXT.");
        } catch (IOException ex) {
            showError("File error", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            showError("Invalid coordinate file", ex.getMessage());
        }
    }

    private void loadCoordsBinary() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        try {
            GraphReader.loadCoordsBinary(chooser.getSelectedFile(), graph);
            coordinateStatus = "BIN coordinates loaded";
            graphPanel.repaint();
            updateStatus("Loaded binary coordinates.");
        } catch (IOException ex) {
            showError("File error", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            showError("Invalid binary coordinate file", ex.getMessage());
        }
    }

    private void saveCoordsTxt() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        try {
            GraphReader.saveCoordsTxt(chooser.getSelectedFile(), graph);
            coordinateStatus = "TXT coordinates saved";
            updateStatus("Saved coordinate TXT.");
        } catch (IOException ex) {
            showError("File error", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            showError("Cannot save coordinates", ex.getMessage());
        }
    }

    private void runCLayout(int algorithmId, String algorithmName) {
        if (currentGraphFile == null) {
            showError("No graph loaded", "Load a graph TXT file before running a layout algorithm.");
            return;
        }

        try {
            Path projectRoot = findProjectRoot();
            Path executable = resolveExecutable(projectRoot);
            if (!Files.isRegularFile(executable)) {
                showError("C executable not found", "C executable not found. Build the C program first with gcc or make.");
                return;
            }

            Path outputDir = projectRoot.resolve("output");
            Files.createDirectories(outputDir);
            Path generatedCoords = outputDir.resolve("java_generated_coords.txt");

            // Java delegates layout computation to the C program through ProcessBuilder.
            ProcessBuilder builder = new ProcessBuilder(Arrays.asList(
                    executable.toString(),
                    currentGraphFile.getAbsolutePath(),
                    generatedCoords.toAbsolutePath().toString(),
                    "text",
                    String.valueOf(algorithmId)
            ));
            builder.directory(projectRoot.toFile());

            Process process = builder.start();
            int exitCode = process.waitFor();
            String stdout = readStream(process.getInputStream());
            String stderr = readStream(process.getErrorStream());

            if (exitCode != 0) {
                showError("C algorithm failed", compactProcessOutput(stdout, stderr));
                return;
            }

            GraphReader.loadCoordsTxt(generatedCoords.toFile(), graph);
            coordinateStatus = "Generated by " + algorithmName;
            algorithmStatus = algorithmName + " finished";
            graphPanel.repaint();
            updateStatus(algorithmName + " finished.");
        } catch (IOException ex) {
            showError("Process error", ex.getMessage());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            showError("Process interrupted", "The C layout process was interrupted.");
        } catch (IllegalArgumentException ex) {
            showError("Invalid generated coordinates", ex.getMessage());
        }
    }

    private Path findProjectRoot() {
        Path current = Paths.get("").toAbsolutePath();
        if (Files.exists(current.resolve("src/main.c"))) {
            return current;
        }

        Path parent = current.getParent();
        if (parent != null && Files.exists(parent.resolve("src/main.c"))) {
            return parent;
        }

        throw new IllegalArgumentException("Cannot find project root. Expected src/main.c in current or parent directory.");
    }

    private Path resolveExecutable(Path projectRoot) {
        String os = System.getProperty("os.name").toLowerCase();
        String executableName = os.contains("win") ? "program.exe" : "program";
        return projectRoot.resolve(executableName);
    }

    private String readStream(InputStream stream) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int read;
        while ((read = stream.read(buffer)) != -1) {
            output.write(buffer, 0, read);
        }
        return output.toString(StandardCharsets.UTF_8.name()).trim();
    }

    private String compactProcessOutput(String stdout, String stderr) {
        String message = "";
        if (!stdout.isEmpty()) message += "stdout:\n" + stdout + "\n";
        if (!stderr.isEmpty()) message += "stderr:\n" + stderr;
        return message.trim().isEmpty() ? "The C program exited with an error." : message.trim();
    }

    private void updateStatus(String action) {
        String graphFile = currentGraphFile == null ? "No graph" : currentGraphFile.getName();
        statusLabel.setText(action + " | " + graphFile +
                " | nodes: " + graph.nodes.size() +
                " | edges: " + graph.edges.size() +
                " | " + coordinateStatus +
                " | " + algorithmStatus);
    }

    private void showError(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }
}
