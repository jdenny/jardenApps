package jarden.life.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jarden.life.Cell;
import jarden.life.CellEnvironment;


/**
 * Created by john.denny@gmail.com on 08/02/2017.
 */

public class TestCell {
    private CellEnvironment cellEnvironment;

    @Before
    public void setUp() throws Exception {
        cellEnvironment = new CellEnvironment(true);
    }

    @After
    public void tearDown() throws Exception {
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
        try {
            Thread.sleep(1000);
            assertEquals(cellEnvironment.getCellCount(), 2);
            Thread.sleep(1000); // give cells time to die for lack of food
            assertEquals(cellEnvironment.getCellCount(), 0);
            Thread.sleep(1000);
            Cell cell2 = Cell.makeSyntheticCell(cellEnvironment);
            cellEnvironment.addFood();
            Thread.sleep(2000); // give cells time to die
            assertEquals(cellEnvironment.getCellCount(), 2); // should both be still alive
            Thread.sleep(1000);
            assertEquals(cellEnvironment.getCellCount(), 0);
            System.out.println("TestCell.consumeResources(); end of test");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
