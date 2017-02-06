package jarden.life.aminoacid;

import jarden.life.Cell;
import jarden.life.nucleicacid.Codon;

/*
 * Obtain an amino acid to match the codon (triplet of nucleotides).
 * This class assumes an unlimited supply of amino acids; will one day have to
 * decide where they come from.
 */
public class GetAminoAcidFromCodon extends AminoAcid {
	private Cell cell;
	
	public GetAminoAcidFromCodon(Cell cell) {
		this.cell = cell;
	}
	public Object action(Object _codon) {
		Codon codon = (Codon)_codon;
		if (codon.isStop()) return codon;
		return cell.waitForAminoAcid(codon);
	}
	public String getName() {
		return "GetAminoAcidFromCodon";
	}
	public boolean matchCodon(Codon codon) {
		return codon.getFirst().getName().equals("Uracil") &&
		codon.getSecond().getName().equals("Uracil") &&
		codon.getThird().getName().equals("Cytosine");
	}
}
