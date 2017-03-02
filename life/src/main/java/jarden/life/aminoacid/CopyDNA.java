package jarden.life.aminoacid;

import jarden.life.Cell;
import jarden.life.nucleicacid.Adenine;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.Cytosine;
import jarden.life.nucleicacid.DNA;
import jarden.life.nucleicacid.Guanine;
import jarden.life.nucleicacid.Nucleotide;
import jarden.life.nucleicacid.Thymine;
import jarden.life.nucleicacid.Uracil;

public class CopyDNA extends AminoAcid {

    @Override
	public DNA action(Object o) throws InterruptedException {
        Cell cell = getCell();
        DNA dna = cell.getDNA();
        DNA dnaCopy = new DNA();
        for (Nucleotide nucleotide: dna) {
            /*!!
            String name;
            if (nucleotide instanceof Adenine) name = "Thymine";
            else if (nucleotide instanceof Thymine) name = "Adenine";
            else if (nucleotide instanceof Guanine) name = "Cytosine";
            else if (nucleotide instanceof Cytosine) name = "Guanine";
            else {
                throw new IllegalArgumentException(
                        "CopyDNA; unrecognised nucleotide: " + nucleotide);
            }
            */
            Nucleotide nucleotideCopy = cell.waitForNucleotide(nucleotide, true);
            if (Thread.interrupted()) {
                Thread.currentThread().interrupt();
                return null;
            }
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
