package jarden.life.aminoacid;

import jarden.life.Cell;
import jarden.life.nucleicacid.Codon;

public abstract class AminoAcid {
    private Cell cell;

	public AminoAcid(Cell cell) {
        this.cell = cell;
    }
    public abstract Object action(Object o); // process next object
    public abstract boolean matchCodon(Codon codon);
    public abstract String getName();

    /**
     * Applies to the first aminoAcid of a protein, in programming terms
     * the control object.
     * @return true means repeat this protein to process the next element
     * in the chain, if there is one.
     * @see #hasMore()
     */
    public boolean isChain() { return false; }

    /**
     * Only used if isChain() returns true.
     * @return true means there are more elements in the chain.
     */
    public boolean hasMore() { return false; }

    /**
     * Applies to the first aminoAcid of a protein, in programming terms
     * the control object. If true, this protein should be run in its own
     * thread, and will be activated whenever its required resources become
     * available.
     * @return true means run this protein in its own thread.
     */
    public boolean keepRunning() { return false; }

    public void setCell(Cell cell) {
        this.cell = cell;
    }
    public Cell getCell() {
        return this.cell;
    }

	/*
	 * Current implementation of codonTable.
	 * See Nucleotide for real-life codonTable.
		AddAminoAcidToProtein	// UUU
		GetAminoAcidFromCodon	// UUC
		GetCodonFromRNA			// UUA
		GetGeneFromDNA 			// UUG
		GetRNAFromGene 			// UCU
		CreateUracil			// UCC
		
		Stop					// UAA
	 */
}
