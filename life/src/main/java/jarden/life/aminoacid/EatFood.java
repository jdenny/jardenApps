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
        Lock foodListLock = cell.getFoodListLock();
        Condition needMoreFood = cell.getNeedMoreFoodCondition();
        foodListLock.lockInterruptibly();
        try {
            while (!cell.needMoreFood()) {
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
        return "EatFood";
    }
    public boolean matchCodon(Codon codon) {
        return codon.getFirst() instanceof Uracil &&
                codon.getSecond() instanceof Guanine &&
                codon.getThird() instanceof Cytosine;
    }
}
