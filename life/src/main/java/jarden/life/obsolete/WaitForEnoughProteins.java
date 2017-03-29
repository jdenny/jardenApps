package jarden.life.obsolete;

import jarden.life.Cell;
import jarden.life.CellResource;
import jarden.life.aminoacid.AminoAcid;
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
    /*!!
    public boolean activateOnCreate() {
        Cell cell = getCell();
        if (cell.isDivideCellRunning()) return false;
        else {
            cell.setDivideCellRunning(true);
            return true;
        }
    }
    */
    @Override
	public CellResource action(CellResource notUsed) throws InterruptedException {
        Cell cell = getCell();
        cell.waitForCellReadyToDivide();
        return null;
	}
    @Override
	public boolean matchCodon(Codon codon) {
        return codon.getFirst() instanceof Uracil &&
                codon.getSecond() instanceof Cytosine &&
                codon.getThird() instanceof Guanine;
    }
    @Override
    public int getIndex() {
        return 24;
    }
    @Override
    public String getName() {
        return "WaitForEnoughProteins";
    }
    @Override
    public String getShortName() {
        return "Wait4Prots";
    }
}
