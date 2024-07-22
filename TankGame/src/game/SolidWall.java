package TankGame.src.game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SolidWall extends GameObject implements Walls {
    private final int x,y;
    private final BufferedImage img;

    public SolidWall(int x, int y, BufferedImage img) {
        super(new Rectangle(x, y, img.getWidth(), img.getHeight()));
        this.x = x;
        this.y = y;
        this.img = img;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
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
        buffer.drawImage(this.img, this.x, this.y, null);
    }

    @Override
    public String toString() {
        return "Solid Wall Location: X = " + x + ", Y = " + y;
    }

    @Override
    public boolean isBreakable() {
        return false;
    }

    @Override
    public void collides(GameObject otherObj) {

    }
}
