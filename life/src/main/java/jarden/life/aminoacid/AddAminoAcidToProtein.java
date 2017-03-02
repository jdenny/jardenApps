package jarden.life.aminoacid;

import jarden.life.Cell;
import jarden.life.Protein;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.Uracil;

public class AddAminoAcidToProtein extends AminoAcid {
	private Protein newProtein;

    @Override
	public Object action(Object _aminoAcidOrCodon) throws InterruptedException {
		if (_aminoAcidOrCodon instanceof Codon) {
            if (newProtein == null) {
                Cell.log("AddAminoAcidToProtein.action(); newProtein is null" +
                        " so ignoring action");
                return null;
            }
			Codon codon = (Codon)_aminoAcidOrCodon;
			if (codon.isStop()) {
				getCell().addProtein(newProtein);
				newProtein = null;
			} else {
				throw new IllegalArgumentException("unexpected codon passed: " + codon);
			}
		}
		else {
			if (newProtein == null) {
				newProtein = new Protein(getCell());
			}
			AminoAcid aminoAcid = (AminoAcid)_aminoAcidOrCodon;
			newProtein.add(aminoAcid);
		}
		return newProtein;
	}
    @Override
	public String getName() {
		return "AddAminoAcidToProtein";
	}
    @Override
	public boolean matchCodon(Codon codon) {
        return codon.getFirst() instanceof Uracil &&
                codon.getSecond() instanceof Uracil &&
                codon.getThird() instanceof Uracil;
    }
    @Override
    public void reset() {
        newProtein = null;
    }

}
