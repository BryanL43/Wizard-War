package TankGame.src.game;

//Observer pattern to update health change or spell change
public interface PlayerHandler {
    void onHealthChange(int newHealth);
    int getCurrentSpell();
    int getSpellsLeft();
    void spellChange(int spellIndex);
    String getSpellName();
    void subtractSpellUsage();
    int getMaxSpell();
}
