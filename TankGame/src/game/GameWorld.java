package TankGame.src.game;


import TankGame.src.GameConstants;
import TankGame.src.Launcher;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
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
    private static List<GameObject> gameObjs = new ArrayList<>(); //static to allow access by tank to add

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
                });

                /*
                 * Sleep for 1000/144 ms (~6.9ms). This is done to have our
                 * loop run at a fixed rate per/sec.
                 */
                Thread.sleep(1000 / 144);
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
                if (obj1.getHitbox().intersects(obj2.getHitbox())) {
                    obj1.collides(obj2);
                    obj2.collides(obj1);
                }
            }
        }
        //Remove inactive bullets
        gameObjs.removeIf(obj -> obj instanceof NormalBullet && !((NormalBullet) obj).isActive());

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

        BufferedImage t1img = null;
        try {
            /*
             * note class loaders read files from the out folder (build folder in Netbeans) and not the
             * current working directory. When running a jar, class loaders will read from within the jar.
             */
            t1img = ImageIO.read(
                    Objects.requireNonNull(GameWorld.class.getClassLoader().getResource("TankGame/resources/tank/tank1.png"),
                            "Could not find tank1.png")
            );
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

        t1 = new Tank(224, 718, 0, 0, (short) 0, t1img);
        TankControl tc1 = new TankControl(t1, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_F);
        this.lf.getJf().addKeyListener(tc1);
        gameObjs.add(t1);

        BufferedImage t2img = null;
        try {
            /*
             * note class loaders read files from the out folder (build folder in Netbeans) and not the
             * current working directory. When running a jar, class loaders will read from within the jar.
             */
            t2img = ImageIO.read(
                    Objects.requireNonNull(GameWorld.class.getClassLoader().getResource("TankGame/resources/tank/tank2.png"),
                            "Could not find tank2.png")
            );
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

        t2 = new Tank(1775, 718, 0, 0, (short) 0, t2img);
        TankControl tc2 = new TankControl(t2, KeyEvent.VK_U, KeyEvent.VK_J, KeyEvent.VK_H, KeyEvent.VK_K, KeyEvent.VK_L);
        this.lf.getJf().addKeyListener(tc2);
        gameObjs.add(t2);

        //Create obstacles
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void drawFloor(Graphics2D buffer) {
        try {
            BufferedImage floor = ImageIO.read(
                    Objects.requireNonNull(GameWorld.class.getClassLoader().getResource("TankGame/resources/floor/Background.bmp"),
                            "Could not get floor texture")
            );

            for (int i = 0; i < GameConstants.GAME_WORLD_WIDTH; i += 320) {
                for (int j = 0; j < GameConstants.GAME_WORLD_HEIGHT; j += 240) {
                    buffer.drawImage(floor, i, j, null);
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    private int calculateScreenX(int x) {
        int screenX = x - (GameConstants.GAME_SCREEN_WIDTH / 4);
        if (screenX < 0) {
            return 0;
        }

        int maxScreenX = GameConstants.GAME_WORLD_WIDTH - GameConstants.GAME_SCREEN_WIDTH;
        if (screenX > maxScreenX) {
            return maxScreenX;
        }

        return screenX;
    }

    private int calculateScreenY(int y) {
        int screenY = y - (GameConstants.GAME_SCREEN_HEIGHT / 2);
        if (screenY < 0) {
            return 0;
        }

        int maxScreenY = GameConstants.GAME_WORLD_HEIGHT - GameConstants.GAME_SCREEN_HEIGHT;
        if (screenY > maxScreenY) {
            return maxScreenY;
        }

        return screenY;
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Graphics2D buffer = world.createGraphics();
        drawFloor(buffer);
        gameObjs.forEach(obj -> obj.drawImage(buffer));

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
    }

    //Need to be static to allow access for tank to add bullet for collision handling
    public static void createBullet(float x, float y, float angle, BufferedImage img) {
        NormalBullet newBullet = new NormalBullet(x, y, angle, img);
        gameObjs.add(newBullet);
    }
}
