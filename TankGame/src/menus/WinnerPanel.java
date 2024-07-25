package TankGame.src.menus;

import TankGame.src.Launcher;
import TankGame.src.ResourceHandler.ResourceManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class WinnerPanel extends JPanel {
    private final BufferedImage menuBackground;
    private final Launcher lf;

    public WinnerPanel(Launcher lf) {
        this.lf = lf;
        menuBackground = ResourceManager.getSprite("title");
        this.setBackground(Color.BLACK);
        this.setLayout(null);

        JButton continueBtn = new JButton("Continue");
        continueBtn.setFont(new Font("Courier New", Font.BOLD, 24));
        continueBtn.setBounds(150, 400, 250, 50);
        continueBtn.addActionListener((actionEvent -> this.lf.closeGame()));

        this.add(continueBtn);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(this.menuBackground, 0, 0, null);
    }
}
