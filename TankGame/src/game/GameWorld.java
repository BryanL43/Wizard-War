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
        this.world = new BufferedImage(GameConstants.GAME_SCREEN_WIDTH,
                GameConstants.GAME_SCREEN_HEIGHT,
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

        t1 = new Tank(300, 300, 0, 0, (short) 0, t1img);
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

        t2 = new Tank(400, 400, 0, 0, (short) 0, t2img);
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

            for (int i = 0; i < GameConstants.GAME_SCREEN_WIDTH; i += 320) {
                for (int j = 0; j < GameConstants.GAME_SCREEN_HEIGHT; j += 240) {
                    buffer.drawImage(floor, i, j, null);
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Graphics2D buffer = world.createGraphics();
        drawFloor(buffer);
        gameObjs.forEach(obj -> obj.drawImage(buffer));

        this.t1.drawImage(buffer);
        this.t2.drawImage(buffer);
        g2.drawImage(world, 0, 0, null);
    }

    //Need to be static to allow access for tank to add bullet for collision handling
    public static void createBullet(float x, float y, float angle, BufferedImage img) {
        NormalBullet newBullet = new NormalBullet(x, y, angle, img);
        gameObjs.add(newBullet);
    }
}
