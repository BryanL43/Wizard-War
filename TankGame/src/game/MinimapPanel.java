package TankGame.src.game;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MinimapPanel extends JPanel {
    private final BufferedImage minimapImage;

    public MinimapPanel(BufferedImage minimapImage) {
        this.minimapImage = minimapImage;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (minimapImage != null) {
            Graphics2D g2 = (Graphics2D) g;
            g2.scale(0.15, 0.15);
            g2.drawImage(minimapImage, 0, 0, null);
        }
    }
}
