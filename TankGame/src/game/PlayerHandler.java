package TankGame.src.game;

//Observer pattern to update health change or spell change
public interface PlayerHandler {
    void onHealthChange(int newHealth);
    int getCurrentSpell();
    void spellChange(int spellIndex);
    String getSpellName();
    int getMaxSpell();
    void setChargeTime(int timeLapsed);
    int getChargeTime();
}
