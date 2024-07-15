package TankGame.src.game;

import TankGame.src.ResourceHandler.Pair;

import java.util.ArrayList;
import java.util.List;

public class Player implements PlayerHandler {
    private int lives;
    private int currentSpell;
    private final Tank playerCharacter;
    private GameWorld game;
    private int chargeTime = 0;

//    private final List<String> spellCarousel = new ArrayList<>();
    //List index for spell rotation and pair for the amount of usage for a specific spell
    private final List<Pair<String, Integer>> spellCarousel = new ArrayList<>();

    public Player(int lives, int spellIndex, Tank playerCharacter, GameWorld game) {
        spellCarousel.add(new Pair<>("magic bullet", 5)); //index 0
        spellCarousel.add(new Pair<>("lightning ball", 2)); //index 1
        spellCarousel.add(new Pair<>("fire ball", 3)); //index 2

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
    public int getSpellsLeft() {
        return this.spellCarousel.get(currentSpell).getR();
    }

    @Override
    public void spellChange(int spellIndex) {
        this.currentSpell = spellIndex;
        game.updateSpellLabel();
    }

    @Override
    public String getSpellName() {
        return spellCarousel.get(currentSpell).getL();
    }

    @Override
    public void subtractSpellUsage() {
        int spellsLeft = spellCarousel.get(currentSpell).getR() - 1;
        this.spellCarousel.get(currentSpell).setR(spellsLeft);
        game.updateSpellLabel();
    }

    @Override
    public int getMaxSpell() {
        return this.spellCarousel.size();
    }
}
