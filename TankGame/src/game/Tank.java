package TankGame.src.game;

import TankGame.src.GameConstants;
import TankGame.src.ResourceHandler.Pair;
import TankGame.src.ResourceHandler.ResourceManager;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.List;

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

    private float R = 2; //1f for casting and shooting
    private float ROTATIONSPEED = 2.5f;

    private BufferedImage img;
    private boolean UpPressed;
    private boolean DownPressed;
    private boolean RightPressed;
    private boolean LeftPressed;

    private boolean ShootPressed = false;
    private long shootStartTime = 0;
    private boolean isShootInProgress = false;
    private long deltaTime;
    private boolean isShooting = false; //Specifically to prevent casting while shooting magic bullet

    private Timer shootTimer;

    private int health = 100;
    private int shield = 0;
    private int castTime = 1300;
    private int id;

    private PlayerHandler playerHandler;
    private boolean stopAttack = false;

    private Animation castingCircle;

    private final List<Pair<String, Long>> powerups = new ArrayList<>();

    long lastAppliedTime = 0;

    Tank(float x, float y, float vx, float vy, float angle, BufferedImage img) {
        super(new Rectangle((int)x, (int)y, img.getWidth(), img.getHeight()));
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.img = img;
        this.angle = angle;
        this.id = (int)(Math.random() * Integer.MAX_VALUE) + 1;
    }

    void setX(float x){
        this.x = x;
    }

    void setY(float y) {
        this.y = y;
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
        if (!isShootInProgress && playerHandler.getSpellsLeft() > 0 && !isShooting) {
            this.ShootPressed = true;
            this.shootStartTime = System.currentTimeMillis();
            this.isShootInProgress = true;
            this.R = 1.2f;
            ImageIcon castingMagicCircle = ResourceManager.getAnimation("magic circle");
            castingCircle = new Animation(castingMagicCircle, (int) this.x - (img.getWidth() / 2), (int) this.y - (img.getHeight() / 2), castTime);
            GameWorld.createAnimation(castingCircle);
        }
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
        if (isShootInProgress) {
            this.ShootPressed = false;
            long elapsedTime = System.currentTimeMillis() - shootStartTime;

            if (elapsedTime >= castTime) {
                String currentSpell = playerHandler.getSpellName();
                BufferedImage bulletImg = ResourceManager.getSprite(currentSpell);

                float offsetDistance = 15;
                double radians = Math.toRadians(angle);

                if (currentSpell.equals("magic bullet")) {
                    isShooting = true;
                    playerHandler.subtractSpellUsage();

                    Timer timer = new Timer();
                    TimerTask mainTask = new TimerTask() {
                        private int count = 0;

                        @Override
                        public void run() {
                            if (count < 5 && !stopAttack) {
                                // Duplicate to track player's movement
                                float tankCenterX = x + img.getWidth() / 4.0f;
                                float tankCenterY = y + img.getHeight() / 4.0f;
                                float bulletX = tankCenterX + ((float) (offsetDistance * Math.cos(radians)));
                                float bulletY = tankCenterY + ((float) (offsetDistance * Math.sin(radians)));

                                GameWorld.createBullet(currentSpell, id, bulletX, bulletY, angle, bulletImg);
                                count++;
                            } else {
                                timer.cancel();
                            }
                        }
                    };
                    timer.schedule(mainTask, 0, 300);

                    //After magic bullet finishes then resume normal speed
                    TimerTask updateRTask = new TimerTask() {
                        @Override
                        public void run() {
                            Tank.this.R = 2;
                            Tank.this.isShooting = false;
                        }
                    };
                    timer.schedule(updateRTask, 1500);
                } else {
                    playerHandler.subtractSpellUsage();

                    //Duplicate to track player's movement
                    float tankCenterX = x + img.getWidth() / 4.0f;
                    float tankCenterY = y + img.getHeight() / 4.0f;
                    float bulletX = tankCenterX + ((float) (offsetDistance * Math.cos(radians)));
                    float bulletY = tankCenterY + ((float) (offsetDistance * Math.sin(radians)));

                    GameWorld.createBullet(currentSpell, id, bulletX, bulletY, angle, bulletImg);
                    this.R = 2;
                }
                deltaTime = 0;
                stopAttack = false;
            } else {
                this.R = 2;
                deltaTime = 0;
            }

            this.isShootInProgress = false;
            this.shootStartTime = 0;
            castingCircle.stopAnimation();

            // Cancel any ongoing timer task if shoot is released early
            if (shootTimer != null) {
                shootTimer.cancel();
                shootTimer = null;
            }
        }
    }

    void changePrevSpell() {
        if (playerHandler.getCurrentSpell() - 1 < 0) {
            playerHandler.spellChange(playerHandler.getMaxSpell() - 1);
            return;
        }
        playerHandler.spellChange(playerHandler.getCurrentSpell() - 1);
    }

    void changeNextSpell() {
        if (playerHandler.getCurrentSpell() + 1 > playerHandler.getMaxSpell() - 1) {
            playerHandler.spellChange(0);
            return;
        }
        playerHandler.spellChange(playerHandler.getCurrentSpell() + 1);
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
            deltaTime = System.currentTimeMillis() - shootStartTime;
        }

        hitbox.setLocation((int) x, (int) y);

        //Power up application
        for (Pair<String, Long> powerup : powerups) {
            if (powerup.getL().equals("health potion") && powerup.getR() > System.currentTimeMillis()) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastAppliedTime >= 1000) {
                    if (health < 100) {
                        health += 2;
                        GameWorld.updateHealthUI();
                    } else {
                        health = 100;
                        GameWorld.updateHealthUI();
                    }
                    lastAppliedTime = currentTime;
                }
            }

            if (powerup.getL().equals("shield potion") && powerup.getR() < System.currentTimeMillis()) {
                shield = 0;
                GameWorld.updateHealthUI();
            }

            if (powerup.getL().equals("casting potion") && powerup.getR() < System.currentTimeMillis()) {
                castTime = 1300;
            }
        }

        //Clear out expired power ups
        powerups.removeIf(powerup -> powerup.getR() < System.currentTimeMillis());
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
        updateCastingCircle();
    }

    private void moveForwards() {
        vx = Math.round(R * Math.cos(Math.toRadians(angle)));
        vy = Math.round(R * Math.sin(Math.toRadians(angle)));
        x += vx;
        y += vy;
        checkBorder();
        updateCastingCircle();
    }

    private void updateCastingCircle() {
        if (castingCircle != null) {
            castingCircle.setPosition((int) this.x - (this.img.getWidth() / 2), (int) this.y - (this.img.getHeight() / 2));
        }
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

    public void takeDamage(int amount) {
        if (shield > 0) {
            if (amount <= shield) {
                shield -= amount;
                amount = 0;
            } else {
                amount -= shield;
                shield = 0;
            }
        }

        if (amount > 0) {
            health -= amount;
        }

        playerHandler.onHealthChange(this.health);
        GameWorld.updateHealthUI();
    }

    public int getHealth() {
        return this.health;
    }

    public void addHealth(int amount) {
        if (this.health + amount <= 100) {
            this.health += amount;
        } else {
            this.health = 100;
        }

        GameWorld.updateHealthUI();
    }

    public void reset() {
        for (int i = 0; i < powerups.size(); i++) {
            powerups.remove(i);
        }
        this.health = 100;
        this.shield = 0;
        this.R = 2;
        this.castTime = 1300;
        playerHandler.resetSpells();
        this.isShootInProgress = false;
        this.isShooting = false;

        GameWorld.updateHealthUI();
    }

    public int getID() {
        return this.id;
    }

    public void setPlayerHandler(PlayerHandler listener) {
        this.playerHandler = listener;
    }

    public void stopAttack() {
        this.stopAttack = true;
    }

    public long getDeltaTime() {
        return this.deltaTime;
    }

    public void setSpeed(float newSpeed) {
        this.R = newSpeed;
    }

    public void resetSpells() {
        this.playerHandler.resetSpells();
    }

    // Ensure that meditate cannot be triggered when firing magic bullets
    public boolean isShootingHappening() {
        return isShooting;
    }

    public void addPowerUp(String type, long expireTime) {
        //Remove repeat power ups. Restricted to 1 stack per unique power up.
        for (int i = 0; i < powerups.size(); i++) {
            if (powerups.get(i).getL().equals(type)) {
                powerups.remove(i);
            }
        }

        powerups.add(new Pair<>(type, expireTime));
    }

    public void setShield(int newShield) {
        this.shield = newShield;
        GameWorld.updateHealthUI();
    }

    public int getShield() {
        return this.shield;
    }

    public void setCastTime(int newCastTime) {
        this.castTime = newCastTime;
    }

    public int getCastTime() {
        return this.castTime;
    }
}