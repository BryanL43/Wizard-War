package TankGame.src.ResourceHandler;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.sound.sampled.AudioInputStream;

public class ResourceManager {
    private final static Map<String, BufferedImage> sprites = new HashMap<>();
    private final static Map<String, ImageIcon> animations = new HashMap<>();
    private final static Map<String, AudioInputStream> audios = new HashMap<>();

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

    private static AudioInputStream loadAudio(String path) throws UnsupportedAudioFileException, IOException {
        return AudioSystem.getAudioInputStream(new File(path).getAbsoluteFile());
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
        ResourceManager.sprites.put("wind blade", loadSprite("TankGame/resources/bullets/windBlade.png"));
        ResourceManager.sprites.put("parchment", loadSprite("TankGame/resources/parchment.jpg"));
        ResourceManager.sprites.put("health potion", loadSprite("TankGame/resources/powerups/healthPotion.png"));
        ResourceManager.sprites.put("shield potion", loadSprite("TankGame/resources/powerups/shieldPotion.png"));
        ResourceManager.sprites.put("bandage", loadSprite("TankGame/resources/powerups/bandage.png"));
        ResourceManager.sprites.put("casting potion", loadSprite("TankGame/resources/powerups/castingPotion.png"));
    }

    private static void initAnimations() {
        ResourceManager.animations.put("explosion", loadAnimation("TankGame/resources/effects/explosion.gif"));
        ResourceManager.animations.put("zap", loadAnimation("TankGame/resources/effects/zapEffect.gif"));
        ResourceManager.animations.put("large explosion", loadAnimation("TankGame/resources/effects/largeExplosion.gif"));
        ResourceManager.animations.put("magic circle", loadAnimation("TankGame/resources/effects/magicCircle.gif"));
        ResourceManager.animations.put("recharge circle", loadAnimation("TankGame/resources/effects/rechargeCircle.gif"));
    }

    private static void initAudios() throws UnsupportedAudioFileException, IOException {
        ResourceManager.audios.put("background", loadAudio("TankGame/resources/BackGroundMusic.wav"));
        ResourceManager.audios.put("explosion", loadAudio("TankGame/resources/soundeffects/Explosion_small.wav"));
    }

    public static void loadResources() throws UnsupportedAudioFileException, IOException {
        ResourceManager.initSprites();
        ResourceManager.initAnimations();
        ResourceManager.initAudios();
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

    public static AudioInputStream getAudio(String name) {
        if (!ResourceManager.audios.containsKey(name)) {
            throw new IllegalArgumentException("Resource " + name + " is not found");
        }
        return ResourceManager.audios.get(name);
    }
}
