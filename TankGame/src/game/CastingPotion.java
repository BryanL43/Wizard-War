package TankGame.src.game;

import TankGame.src.ResourceHandler.Audio;

import java.awt.*;
import java.awt.image.BufferedImage;

public class CastingPotion extends GameObject implements PowerUps {
    private final int x,y;
    private final BufferedImage img;
    private boolean active = true;
    private Audio healthPotSound;

    public CastingPotion(int x, int y, BufferedImage img) {
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
            ((Tank) otherObj).setCastTime((1300 * 3) / 4);
            ((Tank) otherObj).addPowerUp("casting potion", System.currentTimeMillis() + 10000);
            if (healthPotSound == null) {
                healthPotSound = new Audio("health potion", -15f);
            }
            healthPotSound.playAudio();
        }
    }

    @Override
    public boolean isActive() {
        return active;
    }
}
