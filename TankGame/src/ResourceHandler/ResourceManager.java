package TankGame.src.ResourceHandler;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ResourceManager {
    private final static Map<String, BufferedImage> sprites = new HashMap<>();
    private final static Map<String, ImageIcon> animations = new HashMap<>();

    private static BufferedImage loadSprite(String path) {
        try {
            return ImageIO.read(
                    Objects.requireNonNull(ResourceManager.class.getClassLoader().getResource(path),
                            "Failed to acquire " + path)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ImageIcon loadAnimation(String path) {
        return new ImageIcon(
                Objects.requireNonNull(ResourceManager.class.getClassLoader().getResource(path),
                        "Could not get " + path + " animation")
        );
    }

    private static void initSprites() {
        ResourceManager.sprites.put("wizard1", loadSprite("TankGame/resources/character/wizard1.png"));
        ResourceManager.sprites.put("wizard2", loadSprite("TankGame/resources/character/wizard2.png"));
        ResourceManager.sprites.put("floor", loadSprite("TankGame/resources/floor/Background.bmp"));
        ResourceManager.sprites.put("title", loadSprite("TankGame/resources/title.bmp"));
        ResourceManager.sprites.put("solid wall", loadSprite("TankGame/resources/walls/wall1.png"));
        ResourceManager.sprites.put("breakable wall", loadSprite("TankGame/resources/walls/wall2.png"));
        ResourceManager.sprites.put("bullet", loadSprite("TankGame/resources/bullets/Shell.gif"));
        ResourceManager.sprites.put("magic bullet", loadSprite("TankGame/resources/bullets/magicBullet.gif"));
        ResourceManager.sprites.put("lightning ball", loadSprite("TankGame/resources/bullets/lightningBall.png"));
        ResourceManager.sprites.put("fire ball", loadSprite("TankGame/resources/bullets/fireBall.png"));
        ResourceManager.sprites.put("parchment", loadSprite("TankGame/resources/parchment.jpg"));
    }

    private static void initAnimations() {
        ResourceManager.animations.put("explosion", loadAnimation("TankGame/resources/effects/explosion.gif"));
        ResourceManager.animations.put("zap", loadAnimation("TankGame/resources/effects/zapEffect.gif"));
        ResourceManager.animations.put("large explosion", loadAnimation("TankGame/resources/effects/largeExplosion.gif"));
    }

    public static void loadResources() {
        ResourceManager.initSprites();
        ResourceManager.initAnimations();
    }

    public static BufferedImage getSprite(String name) {
        if (!ResourceManager.sprites.containsKey(name)) {
            throw new IllegalArgumentException("Resource " + name + " is not found");
        }
        return ResourceManager.sprites.get(name);
    }

    public static ImageIcon getAnimation(String name) {
        if (!ResourceManager.animations.containsKey(name)) {
            throw new IllegalArgumentException("Resource " + name + " is not found");
        }
        return ResourceManager.animations.get(name);
    }
}
