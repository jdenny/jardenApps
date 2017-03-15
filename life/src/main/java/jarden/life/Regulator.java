package jarden.life;

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

public class Regulator {
    private int regulatorListIndex; // index to this regulator's position within
            // Cell.regulatorList
    private int dnaIndex; // note: dna never changes within
            // a cell; can only change producing new cell
    private int targetCt = 2; // target number of proteins to be built
    private int rnaCt = 1; // number of RNA strands built or being built for protein
    private int proteinCt = 0; // number of this protein built and added to cell

    public Regulator(int dnaIndex, int regulatorListIndex) {
        this.regulatorListIndex = regulatorListIndex;
        this.dnaIndex = dnaIndex;
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
    public int getDnaIndex() {
        return dnaIndex;
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
}
