package TankGame.src.game;

import TankGame.src.GameConstants;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class ZapSpell extends GameObject implements Spell {
    private float x;
    private float y;
    private float vx;
    private float vy;
    private float angle;
    private BufferedImage img;
    private boolean active = true;
    private int parentID;

    private float R = 3;

    ZapSpell(int id, float x, float y, float angle, BufferedImage img) {
        super(new Rectangle((int)x, (int)y, img.getWidth(), img.getHeight()));

        this.x = x;
        this.y = y;
        this.vx = 0;
        this.vy = 0;
        this.angle = angle;
        this.img = img;
        this.parentID = id;
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
        checkBorder();
    }

    private void checkBorder() {
        if (x < 32 || x >= GameConstants.GAME_WORLD_WIDTH - 32 || y < 32 || y >= GameConstants.GAME_WORLD_HEIGHT - 32) {
            this.active = false;
        }
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
        if (otherObj instanceof Tank otherTank) {
            if (otherTank.getID() != parentID) { //Prevent damaging yourself
                this.active = false;
                otherTank.takeDamage(10);
            }
        } else { //Hits any other object like walls
            this.active = false;
        }
    }

    public boolean isActive() {
        return this.active;
    }

    public int getParentID() {
        return this.parentID;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }
}
