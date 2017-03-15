package jarden.life;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import jarden.timer.Timer;
import jarden.timer.TimerListener;

/**
 * Created by john.denny@gmail.com on 01/03/2017.
 */

public class CellEnvironment implements TimerListener {
    public interface FoodListener {
        void onUpdateFood(int foodCount);
    }
    private final Lock foodListLock = new ReentrantLock();
    private final Condition foodAvailable = foodListLock.newCondition();
    private final Lock cellListLock = new ReentrantLock();
    private CellListener cellListener;
    private FoodListener foodListener;

    private List<Cell> cellList = new LinkedList<>();
    private List<Food> foodList = new LinkedList<>();
    private ThreadPoolExecutor threadPoolExecutor =
            (ThreadPoolExecutor) Executors.newCachedThreadPool();
    private int feedInterval = 5; // tenths of a second
    private Timer timer;
    private int deadCellCt;

    public CellEnvironment(boolean startLife)
            throws InterruptedException {
        if (startLife) {
            addCell(Cell.getSyntheticCell(this));
        }
    }
    public void setCellListener(CellListener cellListener) {
        this.cellListener = cellListener;
    }
    public void setFoodListener(FoodListener foodListener) {
        this.foodListener = foodListener;
    }
    /**
     * Interval, in tenths of a second, between adding
     * food to foodList.
     */
    public void setFeedInterval(int interval) {
        this.feedInterval = interval;
        if (timer != null) timer.setInterval(feedInterval);
    }
    public void startFeeding() {
        timer = new Timer(this, feedInterval);
    }
    public void stopFeeding() {
        if (timer != null) timer.stop();
    }
    public void addFood() throws InterruptedException {
        CellFood cellFood = new CellFood();
        cellFood.addAminoAcids();
        cellFood.addNucleotides();
        foodListLock.lockInterruptibly();
        try {
            foodList.add(cellFood);
            foodAvailable.signal();
            notifyFoodListener();
        } finally {
            foodListLock.unlock();
        }
    }
    private void notifyFoodListener() {
        if (foodListener != null) {
            foodListener.onUpdateFood(foodList.size());
        }
    }
    public ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
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
            Food food = foodList.remove(0);
            notifyFoodListener();
            return food;
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
        notifyCellListener();
    }
    private void notifyCellListener() {
        if (cellListener != null) {
            cellListener.onCellCountChanged(cellList.size(), deadCellCt);
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
        notifyCellListener();
    }
    public int getFeedInterval() {
        return feedInterval;
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
    public void exit() {
        System.out.println("CellEnvironment.exit()");
        threadPoolExecutor.shutdownNow();
        try {
            threadPoolExecutor.awaitTermination(5, TimeUnit.SECONDS);
            System.out.println("awaitTermination complete");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
