package jarden.life.aminoacid;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import jarden.life.Cell;
import jarden.life.CellEnvironment;
import jarden.life.CellResource;
import jarden.life.Food;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.Cytosine;
import jarden.life.nucleicacid.Guanine;
import jarden.life.nucleicacid.Uracil;

/**
 * Wait for food from cell's environment, and adds it to
 * the cell's own foodList.
 *
 * Created by john.denny@gmail.com on 2/03/2017.
 */

public class EatFood extends AminoAcid {
    public CellResource action(CellResource notUsed) throws InterruptedException {
        Cell cell = getCell();
        CellEnvironment cellEnvironment = cell.getCellEnvironment();
        Food food = cellEnvironment.waitForFood();
        if (food instanceof Cell) {
            cell.logId("EatFood eating dead cell");
        }
        return food;
    }
    @Override
    public boolean matchCodon(Codon codon) {
        return codon.getFirst() instanceof Uracil &&
                codon.getSecond() instanceof Guanine &&
                codon.getThird() instanceof Cytosine;
    }
    @Override
    public int getIndex() {
        return 23;
    }
    @Override
    public String getName() {
        return "EatFood";
    }
    @Override
    public String getShortName() {
        return "Eat";
    }
}
