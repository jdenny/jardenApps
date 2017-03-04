package jarden.life.aminoacid;

import jarden.life.Cell;
import jarden.life.CellResource;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.DNA;
import jarden.life.nucleicacid.Guanine;
import jarden.life.nucleicacid.Nucleotide;
import jarden.life.nucleicacid.Uracil;

public class CopyDNA extends AminoAcid {

    @Override
	public DNA action(CellResource o) throws InterruptedException {
        Cell cell = getCell();
        DNA dna = cell.getDNA();
        DNA dnaCopy = new DNA();
        for (Nucleotide nucleotide: dna) {
            Nucleotide nucleotideCopy = cell.waitForNucleotide(nucleotide, true);
            dnaCopy.add(nucleotideCopy);
        }
        return dnaCopy;
	}
    @Override
	public String getName() {
		return "CopyDNA";
	}
    @Override
	public boolean matchCodon(Codon codon) {
        return codon.getFirst() instanceof Uracil &&
                codon.getSecond() instanceof Guanine &&
                codon.getThird() instanceof Uracil;
    }
}
