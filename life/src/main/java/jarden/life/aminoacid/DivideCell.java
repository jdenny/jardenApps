package jarden.life.aminoacid;

import java.util.List;

import jarden.life.Cell;
import jarden.life.CellReadyToSplitListener;
import jarden.life.MasterDesigner;
import jarden.life.OnNewCellListener;
import jarden.life.Protein;
import jarden.life.nucleicacid.Adenine;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.Cytosine;
import jarden.life.nucleicacid.DNA;
import jarden.life.nucleicacid.RNA;
import jarden.life.nucleicacid.Uracil;

/**
 * Created by john.denny@gmail.com on 13/02/2017.
 *
 * Create new cell which is identical to this cell.
 * Create new cell; add copy of own DNA; run polymerase & ribosome to
 * create copies of all own proteins, and add these to new cell.
 * @return identical copy of this cell
 */

public class DivideCell extends AminoAcid implements CellReadyToSplitListener {

    /*
    Proposed design:
    when there are enough proteins to divide, stop all threads in this group
    (apart from DivideCell!), wait for them all to stop, then run code
    currently in splitCell(); what really happens?
     */
    public DivideCell(Cell cell) {
        super(cell);
    }

    public Object action(Object object) {
        getCell().waitForEnoughProteins(this);
        /*
        we could splitCell() here, but we've made it a callback method so
        it is done in the same thread as WaitForEnoughProteins, which has
        a lock on proteinList
         */
        return null;
    }
    @Override
    public void splitCell() {
        // TODO: use life to copy DNA
        Cell cell = getCell();
        String dnaStr = cell.getDNA().dnaToString();
        DNA daughterDNA = cell.buildDNAFromString(dnaStr);
        Cell daughterCell = new Cell(daughterDNA);
        daughterCell.setGeneration(cell.getGeneration() + 1);
        List<Protein> proteinList = cell.getProteinList();
        int geneSize = cell.getGeneSize();
        int newProteinCount = proteinList.size();
        for (int i = geneSize; i < newProteinCount; i++) {
            Protein protein = proteinList.remove(geneSize);
            protein.setCell(daughterCell);
            daughterCell.addProtein(protein);
        }
        OnNewCellListener onNewCellListener = cell.getOnNewCellListener();
        if (onNewCellListener != null) {
            daughterCell.setOnNewCellListener(onNewCellListener);
            onNewCellListener.onNewCell(daughterCell);
        }
    }

    public String getName() {
        return "DivideCell";
    }
    public boolean matchCodon(Codon codon) {
        return codon.getFirst() instanceof Uracil &&
                codon.getSecond() instanceof Adenine &&
                codon.getThird() instanceof Cytosine;
    }
}
