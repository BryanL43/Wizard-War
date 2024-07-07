package TankGame.src.game;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public abstract class GameObject {
    protected Rectangle hitbox;

    public GameObject(Rectangle hitbox) {
        this.hitbox = hitbox;
    }

    public static GameObject create(String type, int x, int y) {
        return switch (type) {
            case "9" -> {
                try {
                    BufferedImage solidWall = ImageIO.read(
                            Objects.requireNonNull(GameWorld.class.getClassLoader().getResource("TankGame/resources/walls/wall1.png"),
                                    "Could not get solid wall texture")
                    );
                    yield new SolidWall(x, y, solidWall);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    yield null;
                }
            }
            case "2" -> {
                try {
                    BufferedImage breakableWall = ImageIO.read(
                            Objects.requireNonNull(GameWorld.class.getClassLoader().getResource("TankGame/resources/walls/wall2.png"),
                                    "Could not get breakable wall texture")
                    );
                    yield new BreakableWall(x, y, breakableWall);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    yield null;
                }
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