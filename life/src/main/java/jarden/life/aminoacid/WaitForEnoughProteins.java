package jarden.life.aminoacid;

import java.util.List;
import java.util.concurrent.TimeUnit;
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
        List<Protein> proteinList = cell.getProteinList();
        Lock proteinListLock = cell.getProteinListLock();
        proteinListLock.lockInterruptibly();
        try {
            while (!cell.cellReadyToDivide()) {
                String state = "DivideCell waiting for more proteins";
                cell.logId(state);
                getProtein().setState(state);
                boolean timedOut = !cell.getCellReadyToDivide().await(10, TimeUnit.SECONDS);
                if (timedOut) {
                    cell.logId("WaitForEnoughProteins timed out; ready to die!");
                    // TODO: put this in its own protein KillCell
                    // stopThreads should be method in Cell
                    Protein thisProtein = getProtein();
                    for (Protein protein: proteinList) {
                        if (protein != thisProtein) { // don't stop itself!
                            protein.stop();
                        }
                        /*!!
                        Thread proteinThread = protein.getThread();
                        if (proteinThread != null && proteinThread.isAlive()) {
                            protein.stop();
                            cell.logId("killCell requested stop to protein " + protein);
                            proteinThread.join(300);
                            if (proteinThread.isAlive()) {
                                cell.logId(proteinThread + " didn't die; state=" +
                                        proteinThread.getState() +
                                        "; using forced stop**************");
                                proteinThread.stop();
                            } else {
                                cell.logId("killCell protein stopped: " + protein);
                            }
                        } else {
                            cell.logId("divideCell detected no thread for protein " + protein);
                        }
                    */
                    }
                    cell.getCellEnvironment().removeCell(cell); // TODO: or mark as dead?
                    thisProtein.stop(); // finally, stop itself
                }
            }
            return null; // return now that there are enough proteins to divide
        } finally {
            proteinListLock.unlock();
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