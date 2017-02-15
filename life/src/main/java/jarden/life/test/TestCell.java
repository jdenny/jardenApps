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
    public void splitShouldCreateIdenticalCopy() {
//        Cell daughterCell = syntheticCell.split();
//        assertEquals(syntheticCell, daughterCell);
//        Cell grandDaughterCell = daughterCell.split();
//        assertEquals(syntheticCell, grandDaughterCell);
    }
}
