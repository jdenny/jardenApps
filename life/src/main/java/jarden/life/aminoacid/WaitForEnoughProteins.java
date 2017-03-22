package jarden.life.aminoacid;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import jarden.life.Cell;
import jarden.life.CellResource;
import jarden.life.Protein;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.Cytosine;
import jarden.life.nucleicacid.Guanine;
import jarden.life.nucleicacid.Uracil;

public class WaitForEnoughProteins extends AminoAcid {
    /**
     * Only allow one instance of DivideCell to run for each cell.
     *
     * @return false if another instance of DivideCell is running,
     * otherwise return true.
     */
    public boolean activateOnCreate() {
        Cell cell = getCell();
        if (cell.isDivideCellRunning()) return false;
        else {
            cell.setDivideCellRunning(true);
            return true;
        }
    }
    @Override
	public CellResource action(CellResource notUsed) throws InterruptedException {
        Cell cell = getCell();
        Lock regulatorListLock = cell.getRegulatorListLock();
        Condition cellReadyToDivideCondition =
                cell.getCellReadyToDivideCondition();
        regulatorListLock.lockInterruptibly();
        try {
            String state;
            boolean killIfTimedOut = false; // normal state is true
            while (!cell.cellReadyToDivide()) {
                state = "waiting for cellReadyToDivideCondition";
                cell.logId(state);
                getProtein().setState(state);
                if (killIfTimedOut) {
                    boolean timedOut = !cellReadyToDivideCondition.await(10, TimeUnit.SECONDS);
                    if (timedOut) {
                        cell.logId("WaitForEnoughProteins timed out; ready to die!");
                        // TODO: put this in its own protein KillCell
                        // stopThreads should be method in Cell
                        Protein thisProtein = getProtein();
                        List<Protein> proteinList = cell.getProteinList();
                        for (Protein protein: proteinList) {
                            if (protein != thisProtein) { // don't stop itself!
                                protein.stop();
                            }
                        }
                        cell.getCellEnvironment().removeCell(cell);
                        thisProtein.stop(); // finally, stop itself
                    }
                } else {
                    cellReadyToDivideCondition.await();
                }
            }
            return null;
        } finally {
            regulatorListLock.unlock();
        }
	}

    @Override
	public String getName() {
		return "WaitForEnoughProteins";
	}
    @Override
	public boolean matchCodon(Codon codon) {
        return codon.getFirst() instanceof Uracil &&
                codon.getSecond() instanceof Cytosine &&
                codon.getThird() instanceof Guanine;
    }
}
