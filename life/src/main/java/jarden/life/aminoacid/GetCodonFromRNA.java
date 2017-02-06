package jarden.life.aminoacid;

import jarden.life.Cell;
import jarden.life.nucleicacid.Adenine;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.RNA;
import jarden.life.nucleicacid.Uracil;

/*
 * Get some RNA from cell; for each action, get next codon from rna.
 */
public class GetCodonFromRNA extends AminoAcid {
	private Cell cell;
	private RNA rna;
	private int index;
	
	public GetCodonFromRNA(Cell cell) {
		this.cell = cell;
	}
    @Override
	public Codon action(Object object) {
		if (rna == null || index >= rna.size()) {
			rna = cell.waitForRNA();
			index = 0;
		}
		Codon codon = rna.get(index++);
		if (codon.isStop()) {
			rna = null;
		}
		return codon; // even if it is a stop()!
	}
    @Override
	public String getName() {
		return "GetCodonFromRNA";
	}
    @Override
	public boolean matchCodon(Codon codon) {
        /*!! and do this for rest of aminoAcids!
		return codon.getFirst().getName().equals("Uracil") &&
		codon.getSecond().getName().equals("Uracil") &&
		codon.getThird().getName().equals("Adenine");
		 */
        return codon.getFirst() instanceof Uracil &&
                codon.getSecond() instanceof Uracil &&
                codon.getThird() instanceof Adenine;
	}
    @Override
    public boolean isChain() { return true; }
    @Override
    public boolean hasMore() {
        return rna != null && index < rna.size();
    }
}
