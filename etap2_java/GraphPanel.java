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
    private static final int NODE_RADIUS = 10;

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
            scale = Math.max(0.2, Math.min(5.0, scale * factor));
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
        scale = 1.0;
        offsetX = 0.0;
        offsetY = 0.0;
        repaint();
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
        double worldX = screenToWorldX(screenX);
        double worldY = screenToWorldY(screenY);
        double hitRadius = NODE_RADIUS + 4.0 / scale;

        for (Graph.Node node : graph.nodes.values()) {
            double dx = worldX - node.x;
            double dy = worldY - node.y;
            if (dx * dx + dy * dy <= hitRadius * hitRadius) {
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

        // Graph coordinates are transformed by the current zoom and pan settings.
        g2d.scale(scale, scale);
        g2d.translate(offsetX, offsetY);

        drawEdges(g2d);
        drawNodes(g2d);
        g2d.dispose();
    }

    private void drawEdges(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke((float) (1.4 / scale)));
        g2d.setColor(new Color(120, 120, 120));

        for (Graph.Edge edge : graph.edges) {
            int x1 = (int) Math.round(edge.source.x);
            int y1 = (int) Math.round(edge.source.y);
            int x2 = (int) Math.round(edge.target.x);
            int y2 = (int) Math.round(edge.target.y);
            g2d.drawLine(x1, y1, x2, y2);

            if (showWeights) {
                g2d.setColor(new Color(150, 55, 45));
                g2d.drawString(String.valueOf(edge.weight), (x1 + x2) / 2 + 4, (y1 + y2) / 2 - 4);
                g2d.setColor(new Color(120, 120, 120));
            }
        }
    }

    private void drawNodes(Graphics2D g2d) {
        FontMetrics metrics = g2d.getFontMetrics();

        for (Graph.Node node : graph.nodes.values()) {
            int x = (int) Math.round(node.x);
            int y = (int) Math.round(node.y);

            g2d.setColor(new Color(70, 130, 210));
            g2d.fillOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
            g2d.setColor(new Color(30, 70, 130));
            g2d.drawOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);

            if (showLabels) {
                int labelWidth = metrics.stringWidth(node.id);
                g2d.setColor(new Color(30, 30, 30));
                g2d.drawString(node.id, x - labelWidth / 2, y - NODE_RADIUS - 4);
            }
        }
    }
}
