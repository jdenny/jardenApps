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
    /*!!
    public static String[] aminoAcidNames = {
            "AddAminoAcidToProtein",
            "DigestFood", "DivideCell",
            "FindNextGene", "GetAminoAcidFromCodon",
            "GetCodonFromRNA", "GetRNAFromGene"
    };
    */

    // if amino acid not yet part of a protein, it belongs to the cell
    private Protein protein; // protein this amino acid is part of

    public abstract Object action(Object o); // process next object
    public abstract boolean matchCodon(Codon codon);
    public abstract String getName();
    public String toString() { return getName(); }

    /**
     * Applies to the first aminoAcid of a protein, in programming terms
     * the control object.
     * @return true means start the protein when added to the cell.
     */
    public boolean activateOnCreate() { return true; }

    /**
     * Applies to the first aminoAcid of a protein, in programming terms
     * the control object.
     * @return true means repeat this protein to process the next element
     * in the chain.
     */
    public boolean hasMore() { return false; }

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
}
