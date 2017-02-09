package jarden.life.aminoacid;

import jarden.life.Cell;
import jarden.life.Protein;
import jarden.life.nucleicacid.Adenine;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.Uracil;

public class AddAminoAcidToProtein extends AminoAcid {
	private Protein protein;

	public AddAminoAcidToProtein(Cell cell) {
		super(cell);
	}
	public Object action(Object _aminoAcidOrCodon) {
		if (_aminoAcidOrCodon instanceof Codon) {
			Codon codon = (Codon)_aminoAcidOrCodon;
			if (codon.isStop()) {
				getCell().addProtein(protein);
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
        return codon.getFirst() instanceof Uracil &&
                codon.getSecond() instanceof Uracil &&
                codon.getThird() instanceof Uracil;
    }
}
