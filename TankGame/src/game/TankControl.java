package TankGame.src.game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 *
 * @author anthony-pc
 */
public class TankControl implements KeyListener {
    private final Tank t1;
    private final int up;
    private final int down;
    private final int right;
    private final int left;
    private final int shoot;
    private final int prevSpell;
    private final int nextSpell;
    private final int meditate;

    private boolean canSwitchSpell = true;
    private boolean isMeditating = false;
    private long meditateStartTime;

    public TankControl(Tank t1, int up, int down, int left, int right, int shoot, int prevSpell, int nextSpell, int meditate) {
        this.t1 = t1;
        this.up = up;
        this.down = down;
        this.right = right;
        this.left = left;
        this.shoot = shoot;
        this.prevSpell = prevSpell;
        this.nextSpell = nextSpell;
        this.meditate = meditate;
    }

    @Override
    public void keyTyped(KeyEvent ke) {

    }

    @Override
    public void keyPressed(KeyEvent ke) {
        int keyPressed = ke.getKeyCode();
        if (keyPressed == up) {
            this.t1.toggleUpPressed();
        }
        if (keyPressed == down) {
            this.t1.toggleDownPressed();
        }
        if (keyPressed == left) {
            this.t1.toggleLeftPressed();
        }
        if (keyPressed == right) {
            this.t1.toggleRightPressed();
        }
        if (keyPressed == shoot && !isMeditating) {
            canSwitchSpell = false;
            this.t1.toggleShootPressed();
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        int keyReleased = ke.getKeyCode();
        if (keyReleased  == up) {
            this.t1.unToggleUpPressed();
        }
        if (keyReleased == down) {
            this.t1.unToggleDownPressed();
        }
        if (keyReleased  == left) {
            this.t1.unToggleLeftPressed();
        }
        if (keyReleased  == right) {
            this.t1.unToggleRightPressed();
        }
        if (keyReleased == shoot) {
            canSwitchSpell = true;
            this.t1.unToggleShootPressed();
        }
        if (keyReleased == prevSpell && canSwitchSpell) {
            this.t1.changePrevSpell();
        }
        if (keyReleased == nextSpell && canSwitchSpell) {
            this.t1.changeNextSpell();
        }
        if (keyReleased == meditate) {
            if (!isMeditating) {
                isMeditating = true;
                this.meditateStartTime = System.currentTimeMillis();
                t1.setSpeed(0);

                new Thread((new Runnable() {
                    boolean isMeditating;

                    @Override
                    public void run() {
                        boolean chargeTimeDone = false;
                        while (!chargeTimeDone && isMeditating) {
                            if (System.currentTimeMillis() - meditateStartTime > 3000) {
                                chargeTimeDone = true;
                            }
                        }
                        System.out.println("Broke out of thread");
                        if (chargeTimeDone) {
                            t1.resetSpells();
                            isMeditating = false;
                            t1.setSpeed(2);
                        }
                    }

                    public Runnable pass(boolean isMeditating) {
                        this.isMeditating = isMeditating;
                        return this;
                    }
                }).pass(isMeditating)).start();
            } else {
                isMeditating = false;
                System.out.println("no longer meditating");
                t1.setSpeed(2);
            }
        }
    }
}
