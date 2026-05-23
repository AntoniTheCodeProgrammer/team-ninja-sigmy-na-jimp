package etap2_java;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class GraphPanel extends JPanel {
    private static final int NODE_RADIUS_SCREEN = 4;
    private static final int HIT_RADIUS_SCREEN = 10;
    private static final int LABEL_FONT_SCREEN = 12;
    private static final int WEIGHT_FONT_SCREEN = 11;
    private static final float EDGE_STROKE_SCREEN = 1.0f;

    private final Graph graph;
    private double scale = 1.0;
    private double offsetX = 0.0;
    private double offsetY = 0.0;
    private int dragStartX;
    private int dragStartY;
    private Graph.Node draggedNode;
    private boolean panning;
    private boolean showLabels = true;
    private boolean showWeights = false;

    public GraphPanel(Graph graph) {
        this.graph = graph;
        setBackground(Color.WHITE);

        // Zoom around the mouse pointer so users do not lose the area they are inspecting.
        addMouseWheelListener(e -> {
            double beforeX = screenToWorldX(e.getX());
            double beforeY = screenToWorldY(e.getY());
            double factor = e.getWheelRotation() < 0 ? 1.1 : 1.0 / 1.1;
            scale = Math.max(0.05, Math.min(20.0, scale * factor));
            offsetX = e.getX() / scale - beforeX;
            offsetY = e.getY() / scale - beforeY;
            repaint();
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                draggedNode = findNodeAt(e.getX(), e.getY());
                panning = draggedNode == null;
                dragStartX = e.getX();
                dragStartY = e.getY();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                draggedNode = null;
                panning = false;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (draggedNode != null) {
                    // Dragging a vertex edits its stored coordinates directly.
                    draggedNode.x = screenToWorldX(e.getX());
                    draggedNode.y = screenToWorldY(e.getY());
                } else if (panning) {
                    // Dragging empty space moves the view, not the graph data.
                    offsetX += (e.getX() - dragStartX) / scale;
                    offsetY += (e.getY() - dragStartY) / scale;
                    dragStartX = e.getX();
                    dragStartY = e.getY();
                }
                repaint();
            }
        });
    }

    public void setShowLabels(boolean showLabels) {
        this.showLabels = showLabels;
        repaint();
    }

    public void setShowWeights(boolean showWeights) {
        this.showWeights = showWeights;
        repaint();
    }

    public void resetView() {
        fitGraphToView();
    }

    public void fitGraphToView() {
        if (graph.nodes.isEmpty() || getWidth() <= 0 || getHeight() <= 0) {
            scale = 1.0;
            offsetX = 0.0;
            offsetY = 0.0;
            repaint();
            return;
        }

        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        for (Graph.Node node : graph.nodes.values()) {
            minX = Math.min(minX, node.x);
            maxX = Math.max(maxX, node.x);
            minY = Math.min(minY, node.y);
            maxY = Math.max(maxY, node.y);
        }

        double graphWidth = Math.max(1.0, maxX - minX);
        double graphHeight = Math.max(1.0, maxY - minY);
        double margin = 60.0;
        double availableWidth = Math.max(1.0, getWidth() - 2.0 * margin);
        double availableHeight = Math.max(1.0, getHeight() - 2.0 * margin);

        scale = Math.max(0.05, Math.min(20.0, Math.min(availableWidth / graphWidth, availableHeight / graphHeight)));

        double centerX = (minX + maxX) / 2.0;
        double centerY = (minY + maxY) / 2.0;
        offsetX = getWidth() / (2.0 * scale) - centerX;
        offsetY = getHeight() / (2.0 * scale) - centerY;
        clearInteraction();
        repaint();
    }

    public void clearInteraction() {
        draggedNode = null;
        panning = false;
    }

    double screenToWorldX(int screenX) {
        return screenX / scale - offsetX;
    }

    double screenToWorldY(int screenY) {
        return screenY / scale - offsetY;
    }

    int worldToScreenX(double worldX) {
        return (int) Math.round((worldX + offsetX) * scale);
    }

    int worldToScreenY(double worldY) {
        return (int) Math.round((worldY + offsetY) * scale);
    }

    private Graph.Node findNodeAt(int screenX, int screenY) {
        for (Graph.Node node : graph.nodes.values()) {
            double dx = screenX - worldToScreenX(node.x);
            double dy = screenY - worldToScreenY(node.y);
            if (dx * dx + dy * dy <= HIT_RADIUS_SCREEN * HIT_RADIUS_SCREEN) {
                return node;
            }
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw in screen coordinates so zoom affects positions, not node/text sizes.
        drawEdges(g2d);
        drawNodes(g2d);
        g2d.dispose();
    }

    private void drawEdges(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(EDGE_STROKE_SCREEN));
        g2d.setColor(new Color(120, 120, 120));
        g2d.setFont(g2d.getFont().deriveFont((float) WEIGHT_FONT_SCREEN));

        for (Graph.Edge edge : graph.edges) {
            int x1 = worldToScreenX(edge.source.x);
            int y1 = worldToScreenY(edge.source.y);
            int x2 = worldToScreenX(edge.target.x);
            int y2 = worldToScreenY(edge.target.y);
            g2d.drawLine(x1, y1, x2, y2);

            if (showWeights) {
                g2d.setColor(new Color(120, 70, 55));
                g2d.drawString(String.valueOf(edge.weight), (x1 + x2) / 2 + 5, (y1 + y2) / 2 - 3);
                g2d.setColor(new Color(120, 120, 120));
            }
        }
    }

    private void drawNodes(Graphics2D g2d) {
        g2d.setFont(g2d.getFont().deriveFont((float) LABEL_FONT_SCREEN));
        FontMetrics metrics = g2d.getFontMetrics();

        for (Graph.Node node : graph.nodes.values()) {
            int x = worldToScreenX(node.x);
            int y = worldToScreenY(node.y);

            g2d.setColor(new Color(70, 130, 210));
            g2d.fillOval(x - NODE_RADIUS_SCREEN, y - NODE_RADIUS_SCREEN,
                    NODE_RADIUS_SCREEN * 2, NODE_RADIUS_SCREEN * 2);
            g2d.setColor(new Color(30, 70, 130));
            g2d.drawOval(x - NODE_RADIUS_SCREEN, y - NODE_RADIUS_SCREEN,
                    NODE_RADIUS_SCREEN * 2, NODE_RADIUS_SCREEN * 2);

            if (showLabels) {
                int labelWidth = metrics.stringWidth(node.id);
                g2d.setColor(new Color(30, 30, 30));
                g2d.drawString(node.id, x - labelWidth / 2, y - NODE_RADIUS_SCREEN - 5);
            }
        }
    }
}
