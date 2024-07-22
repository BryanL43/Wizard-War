package TankGame.src.game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class HealthPotion extends GameObject implements PowerUps {
    private final int x,y;
    private final BufferedImage img;
    private boolean active = true;

    public HealthPotion(int x, int y, BufferedImage img) {
        super(new Rectangle(x, y, img.getWidth(), img.getHeight()));
        this.x = x;
        this.y = y;
        this.img = img;
    }

    @Override
    public void drawImage(Graphics buffer) {
        buffer.drawImage(this.img, this.x, this.y, null);
    }

    @Override
    public void collides(GameObject otherObj) {
        if (otherObj instanceof Tank) {
            active = false;
            ((Tank) otherObj).addPowerUp("health potion", System.currentTimeMillis() + 15000);
        }
    }

    @Override
    public boolean isActive() {
        return active;
    }
}
