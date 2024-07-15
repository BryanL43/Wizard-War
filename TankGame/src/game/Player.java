package TankGame.src.game;

import java.util.ArrayList;
import java.util.List;

public class Player implements PlayerHandler {
    private int lives;
    private int currentSpell;
    private final Tank playerCharacter;
    private GameWorld game;
    private int chargeTime = 0;

    private final List<String> spellCarousel = new ArrayList<>();

    public Player(int lives, int spellIndex, Tank playerCharacter, GameWorld game) {
        spellCarousel.add("magic bullet"); //index 0
        spellCarousel.add("lightning ball"); //index 1
        spellCarousel.add("fire ball"); //index 2

        this.lives = lives;
        this.currentSpell = spellIndex;
        this.playerCharacter = playerCharacter;
        this.playerCharacter.setPlayerHandler(this);
        this.game = game; //Need to pass game for resetting
    }

    public int getLives() {
        return this.lives;

    }

    public void loseLife() {
        this.lives--;
        if (this.lives <= 0) {
            System.exit(0);
        }
    }

    @Override
    public void onHealthChange(int newHealth) {
        if (newHealth <= 0) {
            this.lives--;
            if (this.lives <= 0) {
                System.exit(0);
            }
            game.resetGame();
        }
    }

    @Override
    public int getCurrentSpell() {
        return this.currentSpell;
    }

    @Override
    public void spellChange(int spellIndex) {
        this.currentSpell = spellIndex;
        game.updateSpellLabel();
    }

    @Override
    public String getSpellName() {
        return spellCarousel.get(currentSpell);
    }

    @Override
    public int getMaxSpell() {
        return this.spellCarousel.size();
    }

    @Override
    public void setChargeTime(int timeLapsed) {
        this.chargeTime = timeLapsed;
    }

    @Override
    public int getChargeTime() {
        return this.chargeTime;
    }
}
