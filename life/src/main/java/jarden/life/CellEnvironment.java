package jarden.life;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import jarden.timer.Timer;
import jarden.timer.TimerListener;

/**
 * Created by john.denny@gmail.com on 01/03/2017.
 */

public class CellEnvironment implements TimerListener {
    private final Lock foodListLock = new ReentrantLock();
    private final Condition foodAvailable = foodListLock.newCondition();
    private List<Cell> cellList = new LinkedList<>();
    private List<Food> foodList = new LinkedList<>();
    private int feederRate = 5;
    private int nucleotideFeedCt = 5; // i.e. 5 of each
    private int aminoAcidFeedCt = 1; // i.e. 1 of each
    private int dnaFeedCt = 1;
    private int rnaFeedCt = 1; // i.e. 1 of each gene
    private int proteinFeedCt = 1; // i.e. 1 of each
    private Timer timer;

    /**
     * Interval, in tenths of a second, between adding
     * food to foodList.
     * @param rate
     */
    public void setFeederRate(int rate) {
        this.feederRate = rate;
    }
    public void startFeeding() {
        timer = new Timer(this, feederRate);
    }
    public void stopFeeding() {
        timer.stop();
    }
    private void addFood() {
        CellFood cellFood = new CellFood();
        cellFood.addAllAminoAcids(aminoAcidFeedCt);
        cellFood.addAllNucleotides(nucleotideFeedCt);
        foodList.add(cellFood);
    }
    @Override
    public void onTimerEvent() {
        Cell.log("CellEnvironment.onTimerEvent()");
        addFood();
    }
    public Food waitForFood() throws InterruptedException {
        try {
            foodListLock.lockInterruptibly();
            while (true) {
                if (foodList.size() > 0) {
                    return foodList.remove(0);
                }
                Cell.log("CellEnvironment waiting for food ");
                foodAvailable.await();
            }
        } finally {
            foodListLock.unlock();
        }
    }
    public void removeCell(Cell cell) {
        cellList.remove(cell);
    }
}
