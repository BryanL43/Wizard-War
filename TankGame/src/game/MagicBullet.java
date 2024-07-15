package TankGame.src.game;

import TankGame.src.GameConstants;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class MagicBullet extends GameObject implements Spell {
    private float x;
    private float y;
    private float vx;
    private float vy;
    private float angle;
    private BufferedImage img;
    private boolean active = true;
    private int parentID;
    private int bounceLeft = 2;

    private float R = 4;

    MagicBullet(int id, float x, float y, float angle, BufferedImage img) {
        super(new Rectangle((int)x, (int)y, img.getWidth(), img.getHeight()));

        this.x = x;
        this.y = y;
        this.vx = 0;
        this.vy = 0;
        this.angle = angle;
        this.img = img;
        this.parentID = id;
    }

    //Applies an offset to prevent immediate double collision on bounce
    private void applyOffset() {
        x += Math.signum(vx) * 3;
        y += Math.signum(vy) * 3;
        hitbox.setLocation((int) x, (int) y);
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
        if (x < 32 || x >= GameConstants.GAME_WORLD_WIDTH - 32) {
            vx = -vx;
            angle = 180 - angle;
            applyOffset();
            this.bounceLeft--;
        }
        if (y < 32 || y >= GameConstants.GAME_WORLD_HEIGHT - 32) {
            vy = -vy;
            angle = -angle;
            applyOffset();
            this.bounceLeft--;
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
        Rectangle otherHitbox = otherObj.getHitbox();

        if (this.bounceLeft <= 0) {
            this.active = false;
        }

        if (this.hitbox.intersects(otherHitbox)) {
            //Check if the other object is not the tank that fired the bullet
            if (!(otherObj instanceof Tank && ((Tank) otherObj).getID() == this.parentID)) {
                Rectangle intersection = this.hitbox.intersection(otherHitbox);

                if (intersection.width >= intersection.height) { //Reflect vertically
                    vy = -vy;
                    angle = -angle;
                    applyOffset();
                } else { //Reflect horizontally
                    vx = -vx;
                    angle = 180 - angle;
                    applyOffset();
                }

                this.bounceLeft--;

                //Check if the other object is enemy tank
                if (otherObj instanceof Tank) {
                    ((Tank) otherObj).takeDamage(10);
                    this.active = false;
                }
            }
        }
    }

    public boolean isActive() {
        return this.active;
    }
}
