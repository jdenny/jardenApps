package jarden.life;

import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.DNA;
import jarden.life.nucleicacid.RNA;

/**
 * Control the production of a specific protein by turning on and off
 * the gene for that protein. Polymerase uses this class to determine
 * whether or not to produce RNA for the protein. DivideCell uses this
 * class to see if all the proteins have been produced to target.
 * For now, we will set the targetCt to 2; this is in line with the cell
 * dividing when it has 2 of each protein; this may change in the future!
 *
 * Potentially, have start-codon, then proteinCt-codon, where
 * A=0, C=1, G=2, T=3, so 2 could be represented by AAG
 * Created by john.denny@gmail.com on 12/03/2017.
 */

public class Regulator implements /*!!CellResource*/ ChainResource {
    //!! private final DNA dna;
    private final Cell cell;
    // index to this regulator's position within
    // Cell.regulatorList
    private final int regulatorListIndex;
    // note: dna never changes within a cell;
    // can only change producing new cell
    private final int geneStartIndex;
    private final int geneStopIndex;
    private int targetCt = 2; // target number of proteins to be built
    // number of RNA strands built or being built for protein;
    // set to 1 as the daughter cell starts off with one of
    // each protein, donated by the parent cell; parent cell
    // will call addCell() on the daughter cell, without having
    // first built an RNA strand
    private int rnaCt = 1;
    private int proteinCt = 0; // number of this protein built and added to cell
    private int geneIndex; // used while building RNA from this

    public Regulator(Cell cell, int geneStartIndex, int geneStopIndex,
                     int regulatorListIndex) {
        this.cell = cell;
        this.geneStartIndex = geneStartIndex;
        this.geneStopIndex = geneStopIndex;
        geneIndex = geneStartIndex;
        this.regulatorListIndex = regulatorListIndex;
    }
    public boolean rnaBelowTarget() {
        return rnaCt < targetCt;
    }
    public boolean proteinsBelowTarget() {
        return proteinCt < targetCt;
    }
    /**
     * Get the index within DNA to start of this gene,
     * ignoring the start-codon.
     * @return index to start of gene.
     */
    public int getGeneStartIndex() {
        return geneStartIndex;
    }
    public int getRegulatorListIndex() {
        return regulatorListIndex;
    }
    /**
     * Add 1 to rnaCt - number of RNA strands built or being built for protein.
     */
    public void incrementRnaCt() {
        ++rnaCt;
    }
    /**
     * Add 1 to proteinCt - number of this protein built and added to cell.
     */
    public void incrementProteinCt() {
        ++proteinCt;
    }

    /**
     * Subtract 1 from both rnaCt and proteinCt.
     */
    public void decrementCounts() {
        --rnaCt;
        --proteinCt;
    }
    @Override
    public String getName() {
        return "Regulator";
    }
    @Override
    public boolean hasNext() {
        if (geneIndex >= geneStopIndex) {
            geneIndex = geneStartIndex; // i.e. reset for next protein
            return false;
        } else return true;
    }
    @Override
    public CellResource next() {
        DNA dna = cell.getDNA();
        Codon codon = new Codon(
                dna.getFromTemplate(geneIndex++),
                dna.getFromTemplate(geneIndex++),
                dna.getFromTemplate(geneIndex++));
        return codon;
    }
    @Override
    public TargetResource getTargetResource() {
        Protein newProtein = new Protein(cell);
        newProtein.setRegulator(this);
        RNA rna = new RNA();
        rna.setNewProtein(newProtein);
        return rna;
    }
}
