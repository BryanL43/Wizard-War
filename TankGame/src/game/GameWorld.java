package TankGame.src.game;


import TankGame.src.GameConstants;
import TankGame.src.Launcher;
import TankGame.src.ResourceHandler.Audio;
import TankGame.src.ResourceHandler.ResourceManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.List;

/**
 * @author anthony-pc
 */
public class GameWorld extends JPanel implements Runnable {

    private BufferedImage world;
    private static Tank t1;
    private static Tank t2;
    private Player p1;
    private Player p2;
    private final Launcher lf;
    private long tick = 0;
    private static List<GameObject> gameObjs = new ArrayList<>(1000); //static to allow access by tank to add
    private static List<Animation> animations = new ArrayList<>(1000);
    private static List<Audio> audios = new ArrayList<>(1000);
    private boolean aniDebounce = false;

    private JLabel t1Spell = new JLabel();
    private JLabel t2Spell = new JLabel();
    private JPanel window = new JPanel();
    private static JProgressBar healthBar = new JProgressBar(0, 100);
    private static JProgressBar healthBar2 = new JProgressBar(0, 100);
    private static JProgressBar shieldBar = new JProgressBar(0, 100);
    private static JProgressBar shieldBar2 = new JProgressBar(0, 100);
    private static JProgressBar rechargeBar = new JProgressBar(0, 1200);
    private static JProgressBar rechargeBar2 = new JProgressBar(0, 1200);
    private JPanel timerPanel = new JPanel();
    private JLabel timerLabel = new JLabel();
    private int time = 26100;
    private JPanel minimap = new JPanel();
    private MinimapPanel map;

    private JLabel player1Label;
    private JLabel player2Label;

    public GameWorld(Launcher lf) {
        this.lf = lf;
    }

    @Override
    public void run() {
        try {
            while (true) {
                this.tick++;
                if (this.tick == 26100) {
                    p1.loseLife();
                    p2.loseLife();
                    resetGame();
                }
                updateTimer();
                t1.update();
                if (t1.getCastTime() == 1300) {
                    rechargeBar.setValue((int) t1.getDeltaTime());
                } else {
                    rechargeBar.setValue(((int) t1.getDeltaTime() * 5) / 4);
                }
                t2.update();
                if (t2.getCastTime() == 1300) {
                    rechargeBar2.setValue((int) t2.getDeltaTime());
                } else {
                    rechargeBar2.setValue(((int) t2.getDeltaTime() * 5) / 4);
                }
                this.repaint();   // redraw game
                this.checkCollision();
                gameObjs.forEach(obj -> {
                    if (obj instanceof MagicBullet) {
                        ((MagicBullet) obj).update();
                    }
                    if (obj instanceof ZapSpell) {
                        ((ZapSpell) obj).update();
                    }
                    if (obj instanceof FireBallSpell) {
                        ((FireBallSpell) obj).update();
                    }
                    if (obj instanceof WindBladeSpell) {
                        ((WindBladeSpell) obj).update();
                    }
                });

                /*
                 * Sleep for 1000/144 ms (~6.9ms). This is done to have our
                 * loop run at a fixed rate per/sec.
                 */
                Thread.sleep(1000/144);
            }
        } catch (InterruptedException ignored) {
            System.out.println(ignored);
        }
    }

    private void checkCollision() {
        for (int i = 0; i < gameObjs.size(); i++) {
            GameObject obj1 = gameObjs.get(i);
            if (obj1 instanceof Walls) continue; //1st obj != wall so ignore for optimization

            for (int j = 0; j < gameObjs.size(); j++) {
                if (i == j) continue; //if same object then ignore

                GameObject obj2 = gameObjs.get(j);

                //Magic bullet spell bounce effect
                if (obj1 instanceof MagicBullet && !(obj2 instanceof PowerUps)) {
                    if (obj1.getHitbox().intersects(obj2.getHitbox())) {
                        obj1.collides(obj2);

                        if (!aniDebounce && !((MagicBullet) obj1).isActive()) {
                            aniDebounce = true;
                            ImageIcon explosionIcon = ResourceManager.getAnimation("explosion");
                            animations.add(new Animation(explosionIcon, obj1.getHitbox().x, obj1.getHitbox().y, 200));
                        }

                        if (!(obj2 instanceof Spell)) {
                            obj2.collides(obj1);
                        }
                    }
                }

                //Zap spell radius effect
                if (obj1 instanceof ZapSpell && !(obj2 instanceof PowerUps)) {
                    if (obj2 instanceof Tank && ((Tank) obj2).getID() != ((ZapSpell) obj1).getParentID()) { //If other player is near the spell then the spell will trigger AOE damage
                        double distance = Math.sqrt(Math.pow(obj1.getHitbox().getCenterX() - obj2.getHitbox().getCenterX(), 2) +
                                Math.pow(obj1.getHitbox().getCenterY() - obj2.getHitbox().getCenterY(), 2));
                        if (distance <= 90) {
                            obj1.collides(obj2);
                            if (!aniDebounce) {
                                aniDebounce = true;
                                ImageIcon zapEffectIcon = ResourceManager.getAnimation("zap");
                                int zapEffectX = (int) ((ZapSpell) obj1).getX() - (zapEffectIcon.getIconWidth() / 2);
                                int zapEffectY = (int) ((ZapSpell) obj1).getY() - (zapEffectIcon.getIconHeight() / 2);
                                animations.add(new Animation(zapEffectIcon, zapEffectX, zapEffectY, 750));
                            }
                        }
                    } else { //Impact on wall will do nothing
                        if (obj1.getHitbox().intersects(obj2.getHitbox())) {
                            obj1.collides(obj2);

                            if (!(obj2 instanceof Spell)) {
                                obj2.collides(obj1);
                            }
                        }
                    }
                }

                //Fireball explosion effect
                if (obj1 instanceof FireBallSpell && !(obj2 instanceof PowerUps)) {
                    if (obj1.getHitbox().intersects(obj2.getHitbox())) {
                        //Prevent animation from playing on collision with your own tank
                        if (obj2 instanceof Tank && ((Tank) obj2).getID() == ((FireBallSpell) obj1).getParentID()) {
                            continue;
                        }

                        obj1.collides(obj2);
                        if (!aniDebounce) {
                            aniDebounce = true;
                            ImageIcon largeExplosionEffect = ResourceManager.getAnimation("large explosion");
                            int explosionX = (int) ((FireBallSpell) obj1).getX();
                            int explosionY = (int) ((FireBallSpell) obj1).getY() - (largeExplosionEffect.getIconHeight() / 4);
                            Animation explosion = new Animation(largeExplosionEffect, explosionX, explosionY, 750);
                            animations.add(explosion);

                            Audio backgroundMusic = new Audio("explosion", -5.0f);
                            audios.add(backgroundMusic);

                            // Check if enemy tank intersects with the animation bounding box
                            if (obj2 instanceof Tank && obj2.getHitbox().intersects(explosion.getHitBox())) {
                                ((Tank) obj2).takeDamage(10);
                            }
                        }

                        if (!(obj2 instanceof Spell)) {
                            obj2.collides(obj1);
                        }
                    }
                }

                //Wind blade pass through spells and walls
                if (obj1 instanceof WindBladeSpell && !(obj2 instanceof PowerUps)) {
                    if (obj1.getHitbox().intersects(obj2.getHitbox())) {
                        obj1.collides(obj2);
                    }
                    if (!(obj2 instanceof Spell) && obj2 instanceof BreakableWall && obj2.getHitbox().intersects(obj1.getHitbox())) {
                        obj2.collides(obj1);
                    }
                }

                //Tank collided with wall
                if (obj1 instanceof Tank && obj1.getHitbox().intersects(obj2.getHitbox())) {
                    obj1.collides(obj2);
                }

                // Tank collided with power ups
                if (obj2 instanceof PowerUps && obj1.getHitbox().intersects(obj2.getHitbox())) {
                    obj2.collides(obj1);
                }

            }
        }
        aniDebounce = false;

        //Clean up projectiles/destructible (Iterator required for concurrency crash issue)
        Iterator<GameObject> iterator = gameObjs.iterator();
        while (iterator.hasNext()) {
            GameObject obj = iterator.next();
            if ((obj instanceof NormalBullet && !((NormalBullet) obj).isActive()) ||
                    (obj instanceof MagicBullet && !((MagicBullet) obj).isActive()) ||
                    (obj instanceof ZapSpell && !((ZapSpell) obj).isActive()) ||
                    (obj instanceof FireBallSpell && !((FireBallSpell) obj).isActive()) ||
                    (obj instanceof WindBladeSpell && !((WindBladeSpell) obj).isActive()) ||
                    (obj instanceof PowerUps && !((PowerUps) obj).isActive()) ||
                    (obj instanceof BreakableWall && ((BreakableWall) obj).isDestroyed())) {
                iterator.remove();
            }
        }

    }


    /**
     * Reset game to its initial state.
     */
    public void resetGame() {
        this.tick = 0;
        this.time = 26100;

        t1.stopAttack();
        t2.stopAttack();

        t1.setX(224);
        t1.setY(718);
        t1.reset();

        BufferedImage player1Image = ResourceManager.getSprite("wizard1");
        int width1 = player1Image.getWidth() * p1.getLives() / 3;
        int height1 = player1Image.getHeight();
        player1Image = player1Image.getSubimage(0, 0, width1, height1);
        player1Label.setIcon(new ImageIcon(player1Image));
        player1Label.repaint();

        t2.setX(1775);
        t2.setY(718);
        t2.reset();

        BufferedImage player2Image = ResourceManager.getSprite("wizard2");
        int width2 = player2Image.getWidth() * p2.getLives() / 3;
        int height2 = player2Image.getHeight();
        player2Image = player2Image.getSubimage(0, 0, width2, height2);
        player2Label.setIcon(new ImageIcon(player2Image));
        player2Label.repaint();

        //Iterator to prevent thread-related issues
        Iterator<GameObject> iterator = gameObjs.iterator();
        while (iterator.hasNext()) {
            GameObject obj = iterator.next();
            if (!(obj instanceof Tank)) {
                iterator.remove();
            }
        }

        //Remove all animations
        animations.clear();

        //Remove all audio
        for (Audio audio : audios) {
            audio.stopAudio();
        }
        audios.clear();

        InitializeGame();
        updateSpellLabel();
    }

    /**
     * Load all resources for Tank Wars Game. Set all Game Objects to their
     * initial state as well.
     */
    public void InitializeGame() {
        this.world = new BufferedImage(GameConstants.GAME_WORLD_WIDTH,
                GameConstants.GAME_WORLD_HEIGHT,
                BufferedImage.TYPE_INT_RGB);

        if (t1 == null) {
            BufferedImage t1img = ResourceManager.getSprite("wizard1");
            t1 = new Tank(224, 718, 0, 0, (short) 0, t1img);
            TankControl tc1 = new TankControl(t1, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_F, KeyEvent.VK_Q, KeyEvent.VK_E, KeyEvent.VK_R);
            this.lf.getJf().addKeyListener(tc1);
            gameObjs.add(t1);
        }

        if (p1 == null) {
            p1 = new Player(3, 0, t1, this);
        } else {
            p1 = new Player(p1.getLives(), p1.getCurrentSpell(), t1, this);
        }

        if (t2 == null) {
            BufferedImage t2img = ResourceManager.getSprite("wizard2");
            t2 = new Tank(1775, 718, 0, 0, (short) 0, t2img);
            TankControl tc2 = new TankControl(t2, KeyEvent.VK_U, KeyEvent.VK_J, KeyEvent.VK_H, KeyEvent.VK_K, KeyEvent.VK_L, KeyEvent.VK_Y, KeyEvent.VK_I, KeyEvent.VK_O);
            this.lf.getJf().addKeyListener(tc2);
            gameObjs.add(t2);
        }

        if (p2 == null) {
            p2 = new Player(3, 0, t2, this);
        } else {
            p2 = new Player(p2.getLives(), p2.getCurrentSpell(), t2, this);
        }

        //Create the map's walls
        try {
            InputStreamReader mapData = new InputStreamReader(
                    Objects.requireNonNull(GameWorld.class.getClassLoader().getResourceAsStream("TankGame/resources/map/map.csv"),
                            "Could not acquire map data")
            );

            BufferedReader reader = new BufferedReader(mapData);
            String line;
            int row = 0;
            while ((line = reader.readLine()) != null) {
                String[] mapObjects = line.split(",");
                for (int col = 0; col < mapObjects.length; col++) {
                    String type = mapObjects[col];
                    if (type.equals("0")) {
                        continue; //Skip over 0's
                    }
                    gameObjs.add(GameObject.create(type, col * 32, row * 32));
                }
                row++;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        BufferedImage mm = world.getSubimage(0, 0, GameConstants.GAME_WORLD_WIDTH, GameConstants.GAME_WORLD_HEIGHT);
        if (map == null) {
            map = new MinimapPanel(mm);
            map.setBounds((GameConstants.GAME_SCREEN_WIDTH / 2) - 154, (GameConstants.GAME_SCREEN_HEIGHT - 359), 307, 230);
            map.setLayout(null);
            map.setBorder(BorderFactory.createLineBorder(Color.black));
            minimap.add(map);
        } else {
            map.setMapImage(mm);
        }

        Audio backgroundMusic = new Audio("background", -30.0f);
        audios.add(backgroundMusic);
        backgroundMusic.loopAudio();
    }

    private void drawFloor(Graphics2D buffer) {
        BufferedImage floor = ResourceManager.getSprite("floor");
        for (int i = 0; i < GameConstants.GAME_WORLD_WIDTH; i += 320) {
            for (int j = 0; j < GameConstants.GAME_WORLD_HEIGHT; j += 240) {
                buffer.drawImage(floor, i, j, null);
            }
        }
    }

    private int calculateScreenX(int x) {
        int screenX = x - (GameConstants.GAME_SCREEN_WIDTH / 4);
        if (screenX < 0) {
            return 0;
        }

        int maxScreenX = GameConstants.GAME_WORLD_WIDTH - GameConstants.GAME_SCREEN_WIDTH;
        return Math.min(screenX, maxScreenX);

    }

    private int calculateScreenY(int y) {
        int screenY = y - (GameConstants.GAME_SCREEN_HEIGHT / 2);
        if (screenY < 0) {
            return 0;
        }

        int maxScreenY = GameConstants.GAME_WORLD_HEIGHT - GameConstants.GAME_SCREEN_HEIGHT;
        return Math.min(screenY, maxScreenY);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Graphics2D buffer = world.createGraphics();

        drawFloor(buffer);

        // Reverse iteration to prevent skipping
        for (int i = animations.size() - 1; i >= 0; i--) {
            if (animations.get(i).isFinished()) {
                animations.remove(i);
            } else {
                animations.get(i).drawImage(buffer);
            }
        }

        List<GameObject> gameObjsCopy = new ArrayList<>(gameObjs); //Prevents ConcurrentModificationException
        gameObjsCopy.forEach(obj -> obj.drawImage(buffer));

        int screenX1 = calculateScreenX((int) t1.getX());
        int screenX2 = calculateScreenX((int) t2.getX());
        int screenY1 = calculateScreenY((int) t1.getY());
        int screenY2 = calculateScreenY((int) t2.getY());

        //Ensure the requested sub images do not exceed the bounds of the world image
        int halfScreenWidth = GameConstants.GAME_SCREEN_WIDTH / 2;
        int subImageWidth1 = Math.min(halfScreenWidth, GameConstants.GAME_WORLD_WIDTH - screenX1);
        int subImageWidth2 = Math.min(halfScreenWidth, GameConstants.GAME_WORLD_WIDTH - screenX2);

        //Get the sub images of the world centered around the tanks
        BufferedImage leftHalf = world.getSubimage(screenX1, screenY1, subImageWidth1, GameConstants.GAME_SCREEN_HEIGHT);
        BufferedImage rightHalf = world.getSubimage(screenX2, screenY2, subImageWidth2, GameConstants.GAME_SCREEN_HEIGHT);

        //Draw both player's tank screen
        g2.drawImage(leftHalf, 0, 0, null);
        g2.drawImage(rightHalf, halfScreenWidth, 0, null);

        //Draw border between split screen
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1));
        g2.drawLine(halfScreenWidth, 0, halfScreenWidth, GameConstants.GAME_SCREEN_HEIGHT);

        buffer.dispose();
    }

    public void createSubUI() {
        setLayout(new BorderLayout());

        //Create the main window panel
        window.setBackground(Color.gray);
        window.setPreferredSize(new Dimension(GameConstants.GAME_SCREEN_WIDTH, 90));
        window.setLayout(new BorderLayout());
        add(window, BorderLayout.SOUTH);

        //Create the health panel
        JPanel healthPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(ResourceManager.getSprite("parchment"), 0, 0, getWidth(), getHeight(), this);
            }
        };
        healthPanel.setLayout(null);
        healthPanel.setPreferredSize(new Dimension(GameConstants.GAME_SCREEN_WIDTH, 90));

        //Configure and add shield bars to the health panel
        shieldBar.setBounds(((GameConstants.GAME_SCREEN_WIDTH / 4) - 160), 10, 300, 20);
        shieldBar.setValue(0);
        shieldBar.setBackground(new Color(128, 0, 128, 0));
        shieldBar.setForeground(new Color(128, 0, 128));
        shieldBar.setBorderPainted(false);
        healthPanel.add(shieldBar);

        shieldBar2.setBounds(((GameConstants.GAME_SCREEN_WIDTH / 2) + (GameConstants.GAME_SCREEN_WIDTH / 8)), 10, 300, 20);
        shieldBar2.setValue(0);
        shieldBar2.setBackground(new Color(128, 0, 128, 0));
        shieldBar2.setForeground(new Color(128, 0, 128));
        shieldBar2.setBorderPainted(false);
        healthPanel.add(shieldBar2);

        //Configure and add health bars to the health panel
        healthBar.setBounds(((GameConstants.GAME_SCREEN_WIDTH / 4) - 160), 10, 300, 20);
        healthBar.setValue(100);
        healthBar.setBackground(Color.red);
        healthBar.setForeground(Color.green);
        healthBar.setBorderPainted(false);
        healthPanel.add(healthBar);

        healthBar2.setBounds(((GameConstants.GAME_SCREEN_WIDTH / 2) + (GameConstants.GAME_SCREEN_WIDTH / 8)), 10, 300, 20);
        healthBar2.setValue(100);
        healthBar2.setBackground(Color.blue);
        healthBar2.setForeground(Color.green);
        healthBar2.setBorderPainted(false);
        healthPanel.add(healthBar2);

        //Load and add tank images to the health panel
        BufferedImage player1Image = ResourceManager.getSprite("wizard1");
        player1Image = player1Image.getSubimage(0, 0, player1Image.getWidth(), player1Image.getHeight());

        player1Label = new JLabel(new ImageIcon(player1Image));
        player1Label.setBounds(((GameConstants.GAME_SCREEN_WIDTH / 8) - 100), 10, 50, 50);
        healthPanel.add(player1Label);

        BufferedImage player2Image = ResourceManager.getSprite("wizard2");
        player2Image = player2Image.getSubimage(0, 0, player2Image.getWidth(), player2Image.getHeight());

        player2Label = new JLabel(new ImageIcon(player2Image));
        player2Label.setBounds(((GameConstants.GAME_SCREEN_WIDTH / 2) + 50), 10, 50, 50);
        healthPanel.add(player2Label);

        //Add recharge bars to the health panel
        rechargeBar.setBounds(((GameConstants.GAME_SCREEN_WIDTH / 4) - 160), 40, 300, 15);
        rechargeBar.setValue(0);
        rechargeBar.setBackground(Color.lightGray);
        rechargeBar.setForeground(Color.yellow);
        rechargeBar.setBorderPainted(false);
        healthPanel.add(rechargeBar);

        rechargeBar2.setBounds(((GameConstants.GAME_SCREEN_WIDTH / 2) + (GameConstants.GAME_SCREEN_WIDTH / 8)), 40, 300, 15);
        rechargeBar2.setValue(0);
        rechargeBar2.setBackground(Color.lightGray);
        rechargeBar2.setForeground(Color.yellow);
        rechargeBar2.setBorderPainted(false);
        healthPanel.add(rechargeBar2);

        //Add spell labels to the health panel
        t1Spell.setText("<< " + p1.getSpellName() + " (" + p1.getSpellsLeft() + ") " + " >>");
        t1Spell.setForeground(Color.BLACK);
        t1Spell.setBounds(((GameConstants.GAME_SCREEN_WIDTH / 4) - 160), 60, 300, 20);
        t1Spell.setFont(new Font("Arial", Font.BOLD, 20));
        healthPanel.add(t1Spell);

        t2Spell.setText("<< " + p2.getSpellName() + " (" + p2.getSpellsLeft() + ") " + " >>");
        t2Spell.setBounds(((GameConstants.GAME_SCREEN_WIDTH / 2) + (GameConstants.GAME_SCREEN_WIDTH / 8)), 60, 300, 20);
        t2Spell.setForeground(Color.BLACK);
        t2Spell.setFont(new Font("Arial", Font.BOLD, 20));
        healthPanel.add(t2Spell);

        //Add the health panel to the window panel
        window.add(healthPanel, BorderLayout.CENTER);

        //Add the timer panel
        timerPanel.setBackground(Color.gray);
        timerPanel.setLayout(new FlowLayout());
        timerPanel.setBounds(0,0,GameConstants.GAME_SCREEN_WIDTH, GameConstants.GAME_SCREEN_HEIGHT);
        add(timerPanel, BorderLayout.CENTER);

        //Add panel behind timerLabel
        JPanel textPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(ResourceManager.getSprite("parchment"), 0, 0, getWidth(), getHeight(), this);
            }
        };
        textPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        textPanel.setBounds(0, 0, 20, 45);
        timerPanel.add(textPanel);

        //Add the timer label to the timer panel
        timerLabel.setText("Timer: " + time);
        timerLabel.setForeground(Color.black);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        textPanel.add(timerLabel);

        //Add minimap
        minimap.setOpaque(false);
        minimap.setLayout(null);
        add(minimap, BorderLayout.CENTER);

        minimap.setOpaque(false);
        minimap.setLayout(null);
        add(minimap, BorderLayout.CENTER);

        //Panel for minimap
        BufferedImage mm = world.getSubimage(0, 0, GameConstants.GAME_WORLD_WIDTH, GameConstants.GAME_WORLD_HEIGHT);
        MinimapPanel map = new MinimapPanel(mm);
        map.setBounds((GameConstants.GAME_SCREEN_WIDTH / 2) - 154, (GameConstants.GAME_SCREEN_HEIGHT - 356), 307, 230);
        map.setLayout(null);
        map.setBorder(BorderFactory.createLineBorder(Color.black));
        minimap.add(map);

        setComponentZOrder(minimap, 0);

        timerPanel.setOpaque(false);
        window.setVisible(true);
        timerPanel.setVisible(true);
    }

    public static void updateHealthUI() {
        healthBar.setValue(t1.getHealth());
        healthBar2.setValue(t2.getHealth());
        shieldBar.setValue(t1.getShield());
        shieldBar2.setValue(t2.getShield());
    }

    public void updateSpellLabel() {
        t1Spell.setText("<< " + p1.getSpellName() + " (" + p1.getSpellsLeft() + ") " + " >>");
        t2Spell.setText("<< " + p2.getSpellName() + " (" + p2.getSpellsLeft() + ") " + " >>");
    }

    private void updateTimer() {
        int minutes = (time / 145) / 60;
        int seconds = (time / 145) % 60;
        String timeOfGame = String.format("%02d:%02d", minutes, seconds);
        timerLabel.setText(timeOfGame);
        time--;
    }

    //Need to be static to allow access for tank to add bullet for collision handling
    public static void createBullet(String type, int id, float x, float y, float angle, BufferedImage img) {
        Spell newSpell = switch (type) {
            case "magic bullet" -> new MagicBullet(id, x, y, angle, img);
            case "lightning ball" -> new ZapSpell(id, x, y, angle, img);
            case "fire ball" -> new FireBallSpell(id, x, y, angle, img);
            case "wind blade" -> new WindBladeSpell(id, x, y, angle, img);
            default -> throw new IllegalArgumentException("Invalid spell type!");
        };
        gameObjs.add((GameObject) newSpell);
    }

    //Allow access for other objects to create animation
    public static void createAnimation(Animation ani) {
        animations.add(ani);
    }
}
