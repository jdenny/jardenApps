package jarden.life.aminoacid;

import jarden.life.Cell;
import jarden.life.Protein;
import jarden.life.nucleicacid.Codon;

public class AddAminoAcidToProtein extends AminoAcid {
	private Protein protein;
	private Cell cell;
	
	public AddAminoAcidToProtein(Cell cell) {
		this.cell = cell;
	}
	public Object action(Object _aminoAcidOrCodon) {
		if (_aminoAcidOrCodon instanceof Codon) {
			Codon codon = (Codon)_aminoAcidOrCodon;
			if (codon.isStop()) {
				cell.addProtein(protein);
				protein = null;
			} else {
				throw new IllegalArgumentException("unexpected codon passed: " + codon);
			}
		}
		else {
			if (protein == null) {
				protein = new Protein();
			}
			AminoAcid aminoAcid = (AminoAcid)_aminoAcidOrCodon;
			protein.add(aminoAcid);
		}
		return protein;
	}
	public String getName() {
		return "AddAminoAcidToProtein";
	}
	public boolean matchCodon(Codon codon) {
		return codon.getFirst().getName().equals("Uracil") &&
		codon.getSecond().getName().equals("Uracil") &&
		codon.getThird().getName().equals("Uracil");
	}
}
