package jarden.life.aminoacid;

import java.util.List;

import jarden.life.Cell;
import jarden.life.MasterDesigner;
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

public class DivideCell extends AminoAcid implements Cell.CellReadyToSplitListener {
    private int geneSize;

    public DivideCell(Cell cell) {
        super(cell);
    }
    public Object action(Object object) {
        geneSize = getCell().waitForEnoughProteins(this);
        /*
        new constant of geneSize = 4
        if proteinList.size() >= (2 x geneSize), create new cell with
        same DNA, and move extra proteins into it; need some listener
        interface to notify new cell created
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
        List<Protein> proteinList = cell.getProteinList();
        int newProteinCount = proteinList.size();
        for (int i = geneSize; i < newProteinCount; i++) {
            Protein protein = proteinList.remove(geneSize);
            protein.setCell(daughterCell);
            daughterCell.addProtein(protein);
        }
        MasterDesigner.addCell(daughterCell);

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
