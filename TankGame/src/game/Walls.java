package TankGame.src.game;

//Interface for collision handling
public interface Walls {
    int getX();
    int getY();
    int getWidth();
    int getHeight();
    boolean isBreakable();
}