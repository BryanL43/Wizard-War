package TankGame.src.game;

import TankGame.src.GameConstants;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author anthony-pc
 */
public class Tank extends GameObject {

    private float x;
    private float y;
    private float vx;
    private float vy;
    private float angle;

    private float R = 2;
    private float ROTATIONSPEED = 3.0f;

    private BufferedImage img;
    private boolean UpPressed;
    private boolean DownPressed;
    private boolean RightPressed;
    private boolean LeftPressed;
    private boolean ShootPressed = false;

    Tank(float x, float y, float vx, float vy, float angle, BufferedImage img) {
        super(new Rectangle((int)x, (int)y, img.getWidth(), img.getHeight()));
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.img = img;
        this.angle = angle;
    }

    void setX(float x){
        this.x = x;
    }

    void setY(float y) {
        this. y = y;
    }

    float getX() {
        return this.x;
    }

    float getY() {
        return this.y;
    }

    void toggleUpPressed() {
        this.UpPressed = true;
    }

    void toggleDownPressed() {
        this.DownPressed = true;
    }

    void toggleRightPressed() {
        this.RightPressed = true;
    }

    void toggleLeftPressed() {
        this.LeftPressed = true;
    }

    void toggleShootPressed() {
        this.ShootPressed = true;
    }

    void unToggleUpPressed() {
        this.UpPressed = false;
    }

    void unToggleDownPressed() {
        this.DownPressed = false;
    }

    void unToggleRightPressed() {
        this.RightPressed = false;
    }

    void unToggleLeftPressed() {
        this.LeftPressed = false;
    }

    void unToggleShootPressed() {
        this.ShootPressed = false;
    }

    void update() {
        if (this.UpPressed) {
            this.moveForwards();
        }

        if (this.DownPressed) {
            this.moveBackwards();
        }

        if (this.LeftPressed) {
            this.rotateLeft();
        }

        if (this.RightPressed) {
            this.rotateRight();
        }

        if (this.ShootPressed) {
            this.ShootPressed = false;

            BufferedImage bulletImg;
            try {
                bulletImg = ImageIO.read(
                        Objects.requireNonNull(Tank.class.getClassLoader().getResource("TankGame/resources/bullets/Shell.gif"),
                                "Could not find normal shell image")
                );

                float offsetDistance = 58;
                double radians = Math.toRadians(angle);

                float tankCenterX = x + img.getWidth() / 4.0f;
                float tankCenterY = y + img.getHeight() / 4.0f;

                float bulletX = tankCenterX + ((float) (offsetDistance * Math.cos(radians)));
                float bulletY = tankCenterY + ((float) (offsetDistance * Math.sin(radians)));

                GameWorld.createBullet(bulletX, bulletY, angle, bulletImg);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }
        }

        hitbox.setLocation((int) x, (int) y);
    }

    private void rotateLeft() {
        this.angle -= this.ROTATIONSPEED;
    }

    private void rotateRight() {
        this.angle += this.ROTATIONSPEED;
    }

    private void moveBackwards() {
        vx =  Math.round(R * Math.cos(Math.toRadians(angle)));
        vy =  Math.round(R * Math.sin(Math.toRadians(angle)));
        x -= vx;
        y -= vy;
        checkBorder();
    }

    private void moveForwards() {
        vx = Math.round(R * Math.cos(Math.toRadians(angle)));
        vy = Math.round(R * Math.sin(Math.toRadians(angle)));
        x += vx;
        y += vy;
        checkBorder();
    }


    private void checkBorder() {
        if (x < 32) {
            x = 32;
        }
        if (x >= GameConstants.GAME_WORLD_WIDTH - 32) {
            x = GameConstants.GAME_WORLD_WIDTH - 32;
        }
        if (y < 32) {
            y = 32;
        }
        if (y >= GameConstants.GAME_WORLD_HEIGHT - 32) {
            y = GameConstants.GAME_WORLD_HEIGHT - 32;
        }
    }

    @Override
    public String toString() {
        return "Tank: x=" + x + ", y=" + y + ", angle=" + angle;
    }

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

    public void collides(GameObject with) {
        if (with instanceof Walls wall) {

            double tankLeft = this.x;
            double tankTop = this.y;
            double tankRight = this.x + this.img.getWidth();
            double tankBottom = this.y + this.img.getWidth();

            double wallLeft = wall.getX();
            double wallTop = wall.getY();
            double wallRight = wall.getX() + wall.getWidth();
            double wallBottom = wall.getY() + wall.getHeight();

            double horizontalOverlap = Math.min(tankRight - wallLeft, wallRight - tankLeft);
            double verticalOverlap = Math.min(tankBottom - wallTop, wallBottom - tankTop);

            if (horizontalOverlap < verticalOverlap) {
                //Wall horizontal collision
                if (tankRight - wallLeft < wallRight - tankLeft) {
                    //Tank collided the right side of wall
                    this.x = (float) wallLeft - this.img.getWidth();
                } else {
                    //Tank collided the left side of wall
                    this.x = (float) wallRight;
                }
            } else {
                //Wall vertical collision
                if (tankBottom - wallTop < wallBottom - tankTop) {
                    //Tank collided the bottom side of wall
                    this.y = (float) wallTop - this.img.getHeight();
                } else {
                    //Tank collided the top side of wall
                    this.y = (float) wallBottom;
                }
            }
        }
    }
}