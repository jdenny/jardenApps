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
	public DNA action(CellResource notUsed) throws InterruptedException {
        Cell cell = getCell();
        DNA dna = cell.getDNA();
        DNA dnaCopy = new DNA();
        for (Nucleotide nucleotide: dna.getStrand1()) {
            Nucleotide nucleotideCopy = cell.waitForNucleotide(nucleotide, true);
            dnaCopy.addToStrand2(nucleotideCopy);
        }
        cell.logId("dna strand1 copied okay");
        for (Nucleotide nucleotide: dna.getStrand2()) {
            Nucleotide nucleotideCopy = cell.waitForNucleotide(nucleotide, true);
            dnaCopy.addToStrand1(nucleotideCopy);
        }
        cell.logId("dna copied okay");
        return dnaCopy;
	}
    @Override
	public boolean matchCodon(Codon codon) {
        return codon.getFirst() instanceof Uracil &&
                codon.getSecond() instanceof Uracil &&
                codon.getThird() instanceof Guanine;
    }
    @Override
    public int getIndex() {
        return 20;
    }
    @Override
    public String getName() {
        return "CopyDNA";
    }
    @Override
    public String getShortName() {
        return "CopyDNA";
    }
}
