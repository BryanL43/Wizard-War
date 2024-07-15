package TankGame.src.ResourceHandler;

public class Pair<L, R> {
    private final L l; //Spell name
    private R r; //Spell usage left

    public Pair(L l, R r) {
        this.l = l;
        this.r = r;
    }

    public L getL() {
        return l;
    }

    public R getR() {
        return r;
    }

    public void setR(R newR) {
        this.r = newR;
    }
}
