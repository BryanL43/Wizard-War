package TankGame.src.menus;

import TankGame.src.Launcher;
import TankGame.src.ResourceHandler.ResourceManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class PlayerOneWonPanel extends JPanel {
    private final BufferedImage menuBackground;
    private final Launcher lf;

    public PlayerOneWonPanel(Launcher lf) {
        this.lf = lf;
        menuBackground = ResourceManager.getSprite("wizard1");
        this.setBackground(Color.BLACK);
        this.setLayout(null);

        JButton continueBtn = new JButton("Continue");
        continueBtn.setFont(new Font("Courier New", Font.BOLD, 24));
        continueBtn.setBounds(120, 320, 250, 50);
        continueBtn.addActionListener((actionEvent -> this.lf.setFrame("end")));

        this.add(continueBtn);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(this.menuBackground, 0, 0, null);
    }
}
