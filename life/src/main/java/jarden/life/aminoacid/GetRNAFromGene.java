package jarden.life.aminoacid;

import java.util.ListIterator;

import jarden.life.Cell;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.Cytosine;
import jarden.life.nucleicacid.Nucleotide;
import jarden.life.nucleicacid.RNA;
import jarden.life.nucleicacid.Uracil;

/*
 * This protein is called RNA Polymerase.
 * Sequence of DNA:
 * 		regulator gene: builds regulator protein for this operon
 * 		promoter: where polymerase docks; simplified here as TATAAT
 * 		operator: where regulator protein docks
 * 		operon: group of related genes
 * 		terminator: end of operon; simplified here as TAA 
 */
public class GetRNAFromGene extends AminoAcid {

	public GetRNAFromGene(Cell cell) {
        super(cell);
	}
	public Object action(Object _dna) {
		ListIterator<Nucleotide> dna = (ListIterator<Nucleotide>)_dna;  
		RNA rna = new RNA();
        Cell cell = getCell();
		while (dna.hasNext()) {
			String name1 = dna.next().getName();
			if (name1.equals("Thymine")) name1 = "Uracil";
			String name2 = dna.next().getName();
			if (name2.equals("Thymine")) name2 = "Uracil";
			String name3 = dna.next().getName();
			if (name3.equals("Thymine")) name3 = "Uracil";
			Nucleotide first = cell.waitForNucleotide(name1);
            if (Thread.interrupted()) {
                Thread.currentThread().interrupt();
                return null;
            }
			Nucleotide second = cell.waitForNucleotide(name2);
            if (Thread.interrupted()) {
                Thread.currentThread().interrupt();
                return null;
            }
			Nucleotide third = cell.waitForNucleotide(name3);
            if (Thread.interrupted()) {
                Thread.currentThread().interrupt();
                return null;
            }
			Codon codon = new Codon(first, second, third);
			rna.add(codon);
			if (codon.isStop()) break;
		}
		cell.addRNA(rna);
		return null;
	}
	public String getName() {
		return "GetRNAFromGene";
	}
	public boolean matchCodon(Codon codon) {
        return codon.getFirst() instanceof Uracil &&
                codon.getSecond() instanceof Cytosine &&
                codon.getThird() instanceof Uracil;
	}
}
