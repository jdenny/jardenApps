package jarden.life.aminoacid;

import jarden.life.Cell;
import jarden.life.CellResource;
import jarden.life.Protein;
import jarden.life.nucleicacid.Codon;

/**
 * Amino acid is either part of a protein, which in turn is part of
 * a cell; or it is free-standing, and is directly part of a cell.
 * This means that one of cell and protein is null
 */
public abstract class AminoAcid implements CellResource {
    // if amino acid not yet part of a protein, it belongs to the cell
    private Protein protein; // protein this amino acid is part of

    public abstract CellResource action(CellResource resource)
            throws InterruptedException; // process next object
    public abstract boolean matchCodon(Codon codon);
    public abstract int getIndex();
    public abstract String getName();
    public abstract String getShortName();
    public String toString() { return getName(); }
    public boolean isData() { return false; }
    public boolean isCode() { return false; }
    public boolean isBody() { return false; }

    /**
     * Get the protein this aminoAcid belongs to.
     * @return null if this aminoAcid is not yet part of a protein
     */
    public Protein getProtein() {
        return protein;
    }

    public Cell getCell() {
        return this.protein.getCell();
    }

    public void setProtein(Protein protein) {
        this.protein = protein;
    }

    public void reset() {}
}
