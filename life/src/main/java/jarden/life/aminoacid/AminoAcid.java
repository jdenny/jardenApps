package jarden.life.aminoacid;

import jarden.life.nucleicacid.Codon;

public abstract class AminoAcid {
	public abstract Object action(Object o); // process next object
    public abstract boolean matchCodon(Codon codon);
    public abstract String getName();
    public boolean isChain() { return false; }
    public boolean hasMore() { return false; }
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
