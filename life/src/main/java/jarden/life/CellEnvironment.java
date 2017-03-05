package jarden.life;

import java.util.ArrayList;
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
    private final Lock cellListLock = new ReentrantLock();

    private List<Cell> cellList = new LinkedList<>();
    private List<Food> foodList = new LinkedList<>();
    private int feederRate = 5;
    private int nucleotideFeedCt = 5; // i.e. 5 of each
    private int aminoAcidFeedCt = 1; // i.e. 1 of each
    private Timer timer;
    private int deadCellCt;

    public CellEnvironment(boolean startLife) throws InterruptedException {
        if (startLife) {
            addCell(Cell.getSyntheticCell(this));
        }
    }
    /**
     * Interval, in tenths of a second, between adding
     * food to foodList.
     * @param rate
     */
    public void setFeederRate(int rate) {
        this.feederRate = rate;
    }
    public void setNucleotideFeedCt(int nucleotideFeedCt) {
        this.nucleotideFeedCt = nucleotideFeedCt;
    }
    public void setAminoAcidFeedCt(int aminoAcidFeedCt) {
        this.aminoAcidFeedCt = aminoAcidFeedCt;
    }
    public void startFeeding() {
        timer = new Timer(this, feederRate);
    }
    public void stopFeeding() {
        timer.stop();
    }
    public void addFood() throws InterruptedException {
        CellFood cellFood = new CellFood();
        cellFood.addAllAminoAcids(aminoAcidFeedCt);
        cellFood.addAllNucleotides(nucleotideFeedCt);
        foodListLock.lockInterruptibly();
        try {
            foodList.add(cellFood);
            foodAvailable.signal();
        } finally {
            foodListLock.unlock();
        }
    }
    @Override
    public void onTimerEvent() {
        Cell.log("CellEnvironment.onTimerEvent()");
        try {
            addFood();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public Food waitForFood() throws InterruptedException {
        foodListLock.lockInterruptibly();
        try {
            while (foodList.size() == 0) {
                Cell.log("CellEnvironment waiting for food ");
                foodAvailable.await();
            }
            return foodList.remove(0);
        } finally {
            foodListLock.unlock();
        }
    }
    public void removeCell(Cell cell) throws InterruptedException {
        cellListLock.lockInterruptibly();
        try {
            cellList.remove(cell);
            ++deadCellCt;
        } finally {
            cellListLock.unlock();
        }
    }
    public int getCellCount() {
        return cellList.size();
    }
    public int getDeadCellCt() {
        return deadCellCt;
    }
    public List<Cell> getCellList() {
        return cellList;
    }
    public void addCell(Cell newCell) throws InterruptedException {
        cellListLock.lockInterruptibly();
        try {
            cellList.add(newCell);
        } finally {
            cellListLock.unlock();
        }
    }
    public int getFeederRate() {
        return feederRate;
    }
    public int getNucleotideFeedCt() {
        return nucleotideFeedCt;
    }
    public int getAminoAcidFeedCt() {
        return aminoAcidFeedCt;
    }

    public CellData getCellData(int id) {
        for (Cell cell: cellList) {
            if (cell.getId() == id) {
                return cell.getCellData();
            }
        }
        throw new IllegalArgumentException("no cell found for id " + id);
    }

    public List<CellShortData> getCellShortDataList() {
        List<CellShortData> cellShortDataList = new ArrayList<>();
        for (Cell cell: cellList) {
            cellShortDataList.add(cell.getCellShortData());
        }
        return cellShortDataList;
    }
}
