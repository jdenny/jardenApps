package jarden.life;

/**
 * Created by john.denny@gmail.com on 05/03/2017.
 */

public class CellShortData {
    public int id;
    public int generation;
    public int proteinCt;
    public CellShortData(int id, int generation, int proteinCt) {
        this.id = id;
        this.generation = generation;
        this.proteinCt = proteinCt;
    }
    public String toString() {
        return "id=" + id + ", gen=" + generation +
                ", proteinCt=" + proteinCt;
    }
}
