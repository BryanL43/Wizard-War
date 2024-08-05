package TankGame.src.game;

import TankGame.src.GameConstants;
import TankGame.src.ResourceHandler.Audio;
import TankGame.src.ResourceHandler.ResourceManager;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class FireBallSpell extends GameObject implements Spell {
    private float x;
    private float y;
    private float vx;
    private float vy;
    private float angle;
    private BufferedImage img;
    private boolean active = true;
    private int parentID;

    private float R = 4;

    private static Audio fireWhoosh;

    FireBallSpell(int id, float x, float y, float angle, BufferedImage img) {
        super(new Rectangle((int)x, (int)y, img.getWidth(), img.getHeight()));

        this.x = x;
        this.y = y;
        this.vx = 0;
        this.vy = 0;
        this.angle = angle;
        this.img = img;
        this.parentID = id;

        if (fireWhoosh == null) {
            fireWhoosh = new Audio("fire whoosh", 0f);
        }
        fireWhoosh.playAudio();
    }

    @Override
    public void update() {
        if (active) {
            moveForward();
            hitbox.setLocation((int) x, (int) y);
        }
    }

    private void moveForward() {
        vx = (float)(R * Math.cos(Math.toRadians(angle)));
        vy = (float)(R * Math.sin(Math.toRadians(angle)));
        x += vx;
        y += vy;
    }

    @Override
    public String toString() {
        return "Bullet: x=" + x + ", y=" + y + ", angle=" + angle;
    }

    @Override
    public void drawImage(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform originalTransform = g2d.getTransform();

        AffineTransform rotation = AffineTransform.getTranslateInstance(x, y);
        rotation.rotate(Math.toRadians(angle), this.img.getWidth() / 2.0, this.img.getHeight() / 2.0);

        g2d.setTransform(rotation);
        g2d.drawImage(this.img, 0, 0, null);
        Color translucentColor = new Color(0, 0, 0, 0);
        g2d.setColor(translucentColor);
        g2d.drawRect(0, 0, this.img.getWidth(), this.img.getHeight());
        g2d.setTransform(originalTransform);
    }

    @Override
    public void collides(GameObject otherObj) {
        if (!(otherObj instanceof Tank) || ((Tank) otherObj).getID() != this.parentID) {
            this.active = false;
            fireWhoosh.stopAudio();
            ImageIcon largeExplosionEffect = ResourceManager.getAnimation("large explosion");
            int explosionX = (int) this.x;
            int explosionY = (int) this.y - (largeExplosionEffect.getIconHeight() / 4);
            Animation explosion = new Animation(largeExplosionEffect, explosionX, explosionY, 750);
            GameWorld.createAnimation(explosion);
            GameWorld.playAudio("explosion");

            // Check if enemy tank intersects with the animation bounding box
            if (otherObj instanceof Tank && otherObj.getHitbox().intersects(explosion.getHitBox())) {
                ((Tank) otherObj).takeDamage(35);
            }
        }
    }

    @Override
    public boolean isActive() {
        return this.active;
    }
}
