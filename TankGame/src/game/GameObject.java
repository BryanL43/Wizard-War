package TankGame.src.game;
import TankGame.src.ResourceHandler.ResourceManager;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class GameObject {
    protected Rectangle hitbox;

    public GameObject(Rectangle hitbox) {
        this.hitbox = hitbox;
    }

    public static GameObject create(String type, int x, int y) {
        return switch (type) {
            case "9" -> {
                BufferedImage solidWall = ResourceManager.getSprite("solid wall");
                yield new SolidWall(x, y, solidWall);
            }
            case "2" -> {
                BufferedImage breakableWall = ResourceManager.getSprite("breakable wall");
                yield new BreakableWall(x, y, breakableWall);
            }
            default -> throw new IllegalArgumentException("Unexpected type: " + type);
        };
    }

    public abstract void drawImage(Graphics g);

    public Rectangle getHitbox() {
        return this.hitbox.getBounds();
    }

    public abstract void collides(GameObject otherObj);
}