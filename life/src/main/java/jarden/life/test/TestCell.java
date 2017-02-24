package jarden.life.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jarden.life.Cell;
import jarden.life.CellData;
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
        Cell.makeFirstCell(this);
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNewCell(Cell daughterCell) {
        System.out.println("firstCell==daughterCell:" + syntheticCell.equals(daughterCell));
        assertEquals(syntheticCell, daughterCell);
        System.out.println("Assert successful!");

    }

    @Override
    public void onCellUpdated(CellData cellData) {

    }

    @Override
    public void onProteinStatusUpdated(int proteinId, String status) {

    }
}
