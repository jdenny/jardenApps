package jarden.life;

/**
 * Control the production of a specific protein by turning on and off
 * the gene for that protein. Polymerase uses this class to determine
 * whether or not to build each protein. For now, we will set the targetCt
 * to 2; this is in line with the cell dividing when it has 2 of each
 * protein; this may change in the future!
 *
 * Potentially, have start-codon, then proteinCt-codon, where
 * A=0, C=1, G=2, T=3, so 2 could be represented by AAG
 * Created by john.denny@gmail.com on 12/03/2017.
 */

public class Regulator {
    private int dnaIndex; // note: dna never changes within
            // a cell; can only change producing new cell
    private int targetCt = 2; // target number of proteins to be built
    private int actualCt;
    public boolean belowTarget() {
        return actualCt < targetCt;
    }
}
