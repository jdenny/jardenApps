package jarden.life.aminoacid;

import jarden.life.Cell;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.Cytosine;
import jarden.life.nucleicacid.Uracil;

/*
 * Get some RNA from cell; for each action, get next codon from rna.
 */
public class CreateUracil extends AminoAcid {

	public CreateUracil(Cell cell) {
        super(cell);
	}
	public Object action(Object object) {
		Uracil uracil = new Uracil();
		getCell().addNucleotide(uracil);
		return null;
	}
	public String getName() {
		return "CreateUracil";
	}
	public boolean matchCodon(Codon codon) {
        return codon.getFirst() instanceof Uracil &&
                codon.getSecond() instanceof Cytosine &&
                codon.getThird() instanceof Cytosine;
    }
}
