package jarden.life.aminoacid;

import java.util.ListIterator;

import jarden.life.Cell;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.Nucleotide;
import jarden.life.nucleicacid.RNA;

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
	private Cell cell;
	
	public GetRNAFromGene(Cell cell) {
		this.cell = cell;
	}
	public Object action(Object _dna) {
		ListIterator<Nucleotide> dna = (ListIterator<Nucleotide>)_dna;  
		RNA rna = new RNA();
		while (dna.hasNext()) {
			String name1 = dna.next().getName();
			if (name1.equals("Thymine")) name1 = "Uracil";
			String name2 = dna.next().getName();
			if (name2.equals("Thymine")) name2 = "Uracil";
			String name3 = dna.next().getName();
			if (name3.equals("Thymine")) name3 = "Uracil";
			Nucleotide first = cell.waitForNucleotide(name1);
			Nucleotide second = cell.waitForNucleotide(name2);
			Nucleotide third = cell.waitForNucleotide(name3);
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
		return codon.getFirst().getName().equals("Uracil") &&
		codon.getSecond().getName().equals("Cytosine") &&
		codon.getThird().getName().equals("Uracil");
	}
}
