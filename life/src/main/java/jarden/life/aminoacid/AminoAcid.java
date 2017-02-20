package jarden.life.aminoacid;

import jarden.life.Cell;
import jarden.life.Protein;
import jarden.life.nucleicacid.Codon;

/**
 * Amino acid is either part of a protein, which in turn is part of
 * a cell; or it is free-standing, and is directly part of a cell.
 * This means that one of cell and protein is null
 */
public abstract class AminoAcid {
    // if amino acid not yet part of a protein, it belongs to the cell
    private Cell cell;
    private Protein protein; // protein this amino acid is part of

	public AminoAcid(Cell cell) { this.cell = cell; }
    public AminoAcid(Protein protein) { this.protein = protein; }
    public abstract Object action(Object o); // process next object
    public abstract boolean matchCodon(Codon codon);
    public abstract String getName();
    public String toString() { return getName(); }

    /**
     * Applies to the first aminoAcid of a protein, in programming terms
     * the control object.
     * @return true means repeat this protein to process the next element
     * in the chain, if there is one.
     * @see #hasMore()
     */
    public boolean isChain() { return false; }
    /**
     * Applies to the first aminoAcid of a protein, in programming terms
     * the control object.
     * @return true means start the protein when added to the cell.
     */
    public boolean activateOnCreate() { return true; }

    /**
     * Only used if isChain() returns true.
     * @return true means there are more elements in the chain.
     */
    public boolean hasMore() { return false; }

    /*!!
    public boolean keepRunning() { return false; }
    public void setCell(Cell cell) {
        this.cell = cell;
    }
    */

    /**
     *
     * @return null if this aminoAcid is not yet part of a protein
     */
    public Protein getProtein() {
        return protein;
    }
    public Cell getCell() {
        if (this.protein != null) return this.protein.getCell();
        else return cell;
    }

    public void setProtein(Protein protein) {
        this.protein = protein;
        this.cell = null;
    }
}
