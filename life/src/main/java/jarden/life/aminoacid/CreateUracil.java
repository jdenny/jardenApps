package jarden.life.aminoacid;

import jarden.life.Cell;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.Uracil;

/*
 * Get some RNA from cell; for each action, get next codon from rna.
 */
public class CreateUracil extends AminoAcid {
	private Cell cell;
	
	public CreateUracil(Cell cell) {
		this.cell = cell;
	}
	public Object action(Object object) {
		Uracil uracil = new Uracil();
		cell.addNucleotide(uracil);
		return null;
	}
	public String getName() {
		return "CreateUracil";
	}
	public boolean matchCodon(Codon codon) {
		return codon.getFirst().getName().equals("Uracil") &&
		codon.getSecond().getName().equals("Cytosine") &&
		codon.getThird().getName().equals("Cytosine");
	}
}
