package TankGame.src.game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SolidWall extends GameObject implements Walls {
    private final float x,y;
    private final BufferedImage img;

    public SolidWall(float x, float y, BufferedImage img) {
        super(new Rectangle((int)x, (int)y, img.getWidth(), img.getHeight()));
        this.x = x;
        this.y = y;
        this.img = img;
    }

    @Override
    public int getX() {
        return (int)x;
    }

    @Override
    public int getY() {
        return (int)y;
    }

    @Override
    public int getWidth() {
        return img.getWidth();
    }

    @Override
    public int getHeight() {
        return img.getHeight();
    }

    public void drawImage(Graphics buffer) {
        buffer.drawImage(this.img, getX(), getY(), null);
    }

    @Override
    public String toString() {
        return "Wall Location: X = " + x + ", Y = " + y;
    }

    @Override
    public boolean isBreakable() {
        return false;
    }

    @Override
    public void collides(GameObject otherObj) {

    }
}
