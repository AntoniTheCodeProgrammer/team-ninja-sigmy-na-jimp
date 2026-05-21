package etap2_java;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GraphPanel extends JPanel {
    private Graph graph;
    private double scale = 1.0;
    private double offsetX = 0, offsetY = 0;
    private int dragStartX, dragStartY;
    
    public boolean showLabels = true;
    public boolean showWeights = false;

    public GraphPanel(Graph graph) {
        this.graph = graph;
        setBackground(Color.WHITE);

        // Obsługa Zoom
        addMouseWheelListener(e -> {
            if (e.getWheelRotation() < 0) scale *= 1.1;
            else scale /= 1.1;
            repaint();
        });

        // Obsługa Pan (przesuwanie)
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                dragStartX = e.getX();
                dragStartY = e.getY();
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                offsetX += (e.getX() - dragStartX) / scale;
                offsetY += (e.getY() - dragStartY) / scale;
                dragStartX = e.getX();
                dragStartY = e.getY();
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Aplikacja transformacji widoku
        g2d.scale(scale, scale);
        g2d.translate(offsetX, offsetY);

        // Rysowanie krawędzi
        g2d.setColor(Color.GRAY);
        for (Graph.Edge edge : graph.edges) {
            int x1 = (int) edge.source.x;
            int y1 = (int) edge.source.y;
            int x2 = (int) edge.target.x;
            int y2 = (int) edge.target.y;
            g2d.drawLine(x1, y1, x2, y2);
            
            if (showWeights) {
                g2d.setColor(Color.RED);
                g2d.drawString(String.valueOf(edge.weight), (x1+x2)/2, (y1+y2)/2);
                g2d.setColor(Color.GRAY);
            }
        }

        // Rysowanie węzłów
        int radius = 10;
        for (Graph.Node node : graph.nodes.values()) {
            int x = (int) node.x;
            int y = (int) node.y;
            
            g2d.setColor(Color.BLUE);
            g2d.fillOval(x - radius, y - radius, radius * 2, radius * 2);
            
            if (showLabels) {
                g2d.setColor(Color.BLACK);
                g2d.drawString(node.id, x + radius + 2, y - radius - 2);
            }
        }
    }
}