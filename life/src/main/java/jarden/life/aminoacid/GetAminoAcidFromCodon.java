package jarden.life.aminoacid;

import jarden.life.Cell;
import jarden.life.nucleicacid.Adenine;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.Cytosine;
import jarden.life.nucleicacid.Uracil;

/*
 * Obtain an amino acid to match the codon (triplet of nucleotides).
 * This class assumes an unlimited supply of amino acids; will one day have to
 * decide where they come from.
 */
public class GetAminoAcidFromCodon extends AminoAcid {

	public GetAminoAcidFromCodon(Cell cell) {
		super(cell);
	}
	public Object action(Object _codon) {
		Codon codon = (Codon)_codon;
		if (codon.isStop()) return codon;
		return getCell().waitForAminoAcid(codon);
	}
	public String getName() {
		return "GetAminoAcidFromCodon";
	}
	public boolean matchCodon(Codon codon) {
        return codon.getFirst() instanceof Uracil &&
                codon.getSecond() instanceof Uracil &&
                codon.getThird() instanceof Cytosine;
	}
}
