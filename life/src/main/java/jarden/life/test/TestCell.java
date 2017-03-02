package jarden.life.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jarden.life.Cell;
import jarden.life.CellListener;


/**
 * Created by john.denny@gmail.com on 08/02/2017.
 */

public class TestCell implements CellListener {
    private Cell syntheticCell;

    @Before
    public void setUp() throws Exception {
        this.syntheticCell = Cell.getSyntheticCell();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void firstCellShouldEqualSyntheticCell() {
        try {
            Cell.makeFirstCell(this);
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void consumeResources() {
        /*
        E.g. 10 units of food per second; cells need to feed
        at least once per second; so enough food to feed
        roughly 10 cells?
        Tasks:
        1. new class CellEnvironment:
            collection of cells
            collection of food
        2. each cell belongs to environment
        3. new protein EatFood, which waits for food from its
            environment, and adds it to the cell's own foodList
        4. waitForEnoughProteins has a time limit; if limit
           exceeded, the cell dies
        5. add targets to production of resources
           see Design.txt, Current proposal

        Test:
        get synthetic cell
        this should produce a daughter cell
        both should die for lack of food after certain time
        get another synthetic cell
        check produces daughter cell
        add food to environment; both should live longer,
         to produce more cells, then all die
         */
    }

    @Override
    public void onNewCell(Cell daughterCell) {
        System.out.println("firstCell.isCopy(daughterCell):" +
                syntheticCell.isCopy(daughterCell));
        assertTrue(daughterCell.isCopy(syntheticCell));
        assertTrue(syntheticCell.isCopy(daughterCell));
        System.out.println("Assert successful!");

    }

    @Override
    public void onCellUpdated(int cellId) {

    }

    @Override
    public void onProteinStatusUpdated(int proteinId, String status) {

    }
}
