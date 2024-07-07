package TankGame.src.game;

public interface Walls {
    int getX();
    int getY();
    int getWidth();
    int getHeight();
    boolean isBreakable();
}