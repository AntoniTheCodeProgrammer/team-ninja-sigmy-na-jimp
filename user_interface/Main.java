package etap2_java;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GraphApp app = new GraphApp();
            app.setVisible(true);
        });
    }
}