package TankGame.src.game;

import javax.swing.*;
import java.awt.*;

public class Animation {
    private final ImageIcon animatedIcon;
    private int x,y;
    private Timer timer;
    private boolean finished = false;

    public Animation(ImageIcon img, int x, int y, int delay) {
        this.animatedIcon = img;
        this.x = x;
        this.y = y;
        this.timer = new Timer(delay, e -> {
            finished = true;
            timer.stop();
        });
        this.timer.setRepeats(false);
        this.timer.start();
    }

    public boolean isFinished() {
        return finished;
    }

    public void drawImage(Graphics2D g) {
        if (!finished) {
            g.drawImage(animatedIcon.getImage(), x, y, null);
        }
    }

    public Rectangle getHitBox() {
        return new Rectangle(x, y, animatedIcon.getIconWidth() * 2, animatedIcon.getIconHeight() * 2);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void stopAnimation() {
        this.finished = true;
        this.timer.stop();
    }
}
