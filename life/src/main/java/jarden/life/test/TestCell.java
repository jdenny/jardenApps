package jarden.life.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jarden.life.Cell;


/**
 * Created by john.denny@gmail.com on 08/02/2017.
 */

public class TestCell {
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
        Cell.makeFirstCell(daughterCell -> {
            System.out.println("firstCell==daughterCell:" + syntheticCell.equals(daughterCell));
            assertEquals(syntheticCell, daughterCell);
            System.out.println("Assert successful!");
        });
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
