package TankGame.src.game;

import TankGame.src.ResourceHandler.Audio;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ShieldPotion extends GameObject implements PowerUps {
    private final int x,y;
    private final BufferedImage img;
    private boolean active = true;
    private Audio shieldPotSound;

    public ShieldPotion(int x, int y, BufferedImage img) {
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
            ((Tank) otherObj).setShield(20);
            ((Tank) otherObj).addPowerUp("shield potion", System.currentTimeMillis() + 20000);
            if (shieldPotSound == null) {
                shieldPotSound = new Audio("shield potion", -15f);
            }
            shieldPotSound.playAudio();
        }
    }

    @Override
    public boolean isActive() {
        return active;
    }
}
