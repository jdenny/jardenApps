package jarden.life.aminoacid;

import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import jarden.life.Cell;
import jarden.life.CellEnvironment;
import jarden.life.Food;
import jarden.life.Protein;
import jarden.life.nucleicacid.Adenine;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.Cytosine;
import jarden.life.nucleicacid.DNA;
import jarden.life.nucleicacid.Nucleotide;
import jarden.life.nucleicacid.Uracil;

/**
 * Wait for food from cell's environment, and adds it to
 * the cell's own foodList.
 *
 * Created by john.denny@gmail.com on 2/03/2017.
 */

public class EatFood extends AminoAcid {
    public Object action(Object object) throws InterruptedException {
        Cell cell = getCell();
        Condition needMoreFood = cell.getNeedMoreFood();
        Lock foodListLock = cell.getFoodListLock();
        foodListLock.lockInterruptibly();
        try {
            while (cell.needsMoreResources()) {
                cell.logId("waiting for needMoreFood");
                needMoreFood.await();
            }
        } finally {
            foodListLock.unlock();
        }
        CellEnvironment cellEnvironment = cell.getCellEnvironment();
        Food food = cellEnvironment.waitForFood();
        cell.addFood(food);
        return null;
    }
    public String getName() {
        return "DigestFood";
    }
    public boolean matchCodon(Codon codon) {
        return codon.getFirst() instanceof Uracil &&
                codon.getSecond() instanceof Cytosine &&
                codon.getThird() instanceof Adenine;
    }
}
