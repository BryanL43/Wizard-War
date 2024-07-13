package TankGame.src.game;


import TankGame.src.GameConstants;
import TankGame.src.Launcher;
import TankGame.src.ResourceHandler.ResourceManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author anthony-pc
 */
public class GameWorld extends JPanel implements Runnable {

    private BufferedImage world;
    private Tank t1;
    private Tank t2;
    private final Launcher lf;
    private long tick = 0;
    private static final List<GameObject> gameObjs = new ArrayList<>(); //static to allow access by tank to add
    private final List<Animation> animations = new ArrayList<>();
    private boolean aniDebounce = false;

    public GameWorld(Launcher lf) {
        this.lf = lf;
    }

    @Override
    public void run() {
        try {
            while (true) {
                this.tick++;
                this.t1.update();
                this.t2.update(); // update tank
                this.repaint();   // redraw game
                this.checkCollision();
                gameObjs.forEach(obj -> {
                    if (obj instanceof NormalBullet) {
                        ((NormalBullet) obj).update();
                    }
                    if (obj instanceof MagicBullet) {
                        ((MagicBullet) obj).update();
                    }
                    if (obj instanceof ZapBullet) {
                        ((ZapBullet) obj).update();
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
                if (!(obj1 instanceof ZapBullet)) {
                    if (obj1.getHitbox().intersects(obj2.getHitbox())) {
                        obj1.collides(obj2);

                        if (!aniDebounce &&
                                (obj1 instanceof MagicBullet) && !((MagicBullet) obj1).isActive() ||
                                (obj1 instanceof NormalBullet && !((NormalBullet) obj1).isActive())) {

                            aniDebounce = true;
                            ImageIcon explosionIcon = ResourceManager.getAnimation("explosion");
                            animations.add(new Animation(explosionIcon, obj1.getHitbox().x, obj1.getHitbox().y, 200));
                        }

                        if (!(obj2 instanceof Bullet)) {
                            obj2.collides(obj1);
                        }
                    }
                } else {
                    if (obj2 instanceof Tank && ((Tank) obj2).getID() != ((ZapBullet) obj1).getParentID()) {
                        double distance = Math.sqrt(Math.pow(obj1.getHitbox().getCenterX() - obj2.getHitbox().getCenterX(), 2) +
                                Math.pow(obj1.getHitbox().getCenterY() - obj2.getHitbox().getCenterY(), 2));
                        if (distance <= 90) {
                            obj1.collides(obj2);
                            if (!aniDebounce) {
                                aniDebounce = true;
                                ImageIcon zapEffectIcon = ResourceManager.getAnimation("zap");
                                animations.add(new Animation(zapEffectIcon, (int) ((ZapBullet) obj1).getX() - (zapEffectIcon.getIconWidth() / 2), (int) ((ZapBullet) obj1).getY() - (zapEffectIcon.getIconHeight() / 2), 750));
                            }
                        }
                    } else {
                        if (obj1.getHitbox().intersects(obj2.getHitbox())) {
                            obj1.collides(obj2);

                            if (!(obj2 instanceof Bullet)) {
                                obj2.collides(obj1);
                            }
                        }
                    }
                }
            }
        }
        aniDebounce = false;

        //Remove inactive bullets
        gameObjs.removeIf(obj -> obj instanceof NormalBullet && !((NormalBullet) obj).isActive());
        gameObjs.removeIf(obj -> obj instanceof MagicBullet && !((MagicBullet) obj).isActive());
        gameObjs.removeIf(obj -> obj instanceof ZapBullet && !((ZapBullet) obj).isActive());

        //Remove breakable wall if hit
        gameObjs.removeIf(obj -> obj instanceof BreakableWall && ((BreakableWall) obj).isDestroyed());
    }


    /**
     * Reset game to its initial state.
     */
    public void resetGame() {
        this.tick = 0;
        this.t1.setX(300);
        this.t1.setY(300);
    }

    /**
     * Load all resources for Tank Wars Game. Set all Game Objects to their
     * initial state as well.
     */
    public void InitializeGame() {
        this.world = new BufferedImage(GameConstants.GAME_WORLD_WIDTH,
                GameConstants.GAME_WORLD_HEIGHT,
                BufferedImage.TYPE_INT_RGB);

        BufferedImage t1img = ResourceManager.getSprite("tank1");
        t1 = new Tank(224, 718, 0, 0, (short) 0, t1img);
        TankControl tc1 = new TankControl(t1, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_F);
        this.lf.getJf().addKeyListener(tc1);
        gameObjs.add(t1);

        BufferedImage t2img = ResourceManager.getSprite("tank2");
        t2 = new Tank(1775, 718, 0, 0, (short) 0, t2img);
        TankControl tc2 = new TankControl(t2, KeyEvent.VK_U, KeyEvent.VK_J, KeyEvent.VK_H, KeyEvent.VK_K, KeyEvent.VK_L);
        this.lf.getJf().addKeyListener(tc2);
        gameObjs.add(t2);

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

        List<GameObject> gameObjsCopy = new ArrayList<>(gameObjs); //Prevents ConcurrentModificationException
        gameObjsCopy.forEach(obj -> obj.drawImage(buffer));

        //Reverse iteration to prevent skipping
        for (int i = animations.size() - 1; i >= 0; i--) {
            if (animations.get(i).isFinished()) {
                animations.remove(i);
            } else {
                animations.get(i).drawImage(buffer);
            }
        }

        int screenX1 = calculateScreenX((int) this.t1.getX());
        int screenX2 = calculateScreenX((int) this.t2.getX());
        int screenY1 = calculateScreenY((int) this.t1.getY());
        int screenY2 = calculateScreenY((int) this.t2.getY());

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
        int borderThickness = 10;
        g2.setStroke(new BasicStroke(borderThickness));
        g2.drawLine(halfScreenWidth - borderThickness / 2, 0, halfScreenWidth - borderThickness / 2, GameConstants.GAME_SCREEN_HEIGHT);

        buffer.dispose();
    }

    //Need to be static to allow access for tank to add bullet for collision handling
    public static void createBullet(int id, float x, float y, float angle, BufferedImage img) {
//        NormalBullet newBullet = new NormalBullet(id, x, y, angle, img);
//        MagicBullet newBullet = new MagicBullet(id, x, y, angle, img);
        ZapBullet newBullet = new ZapBullet(id, x, y, angle, img);
        gameObjs.add(newBullet);
    }
}
