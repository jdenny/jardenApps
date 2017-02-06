package jarden.life.aminoacid;

import java.util.ListIterator;

import jarden.life.Cell;
import jarden.life.nucleicacid.Adenine;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.Cytosine;
import jarden.life.nucleicacid.DNA;
import jarden.life.nucleicacid.Guanine;
import jarden.life.nucleicacid.NucleicAcid;
import jarden.life.nucleicacid.Nucleotide;
import jarden.life.nucleicacid.Thymine;
import static jarden.life.nucleicacid.NucleicAcid.promoterCodes;
import static jarden.life.nucleicacid.NucleicAcid.terminatorCodes;

/*
 * This protein is called RNA Polymerase.
 * Sequence of DNA:
 * 		regulator gene: builds regulator protein for this operon
 * 		promoter: where polymerase docks; simplified here as TATAAT
 * 		operator: where regulator protein docks
 * 		operon: group of related genes
 * 		terminator: end of operon; simplified here as UAA 
 */
public class GetGeneFromDNA extends AminoAcid {
	private Cell cell;
	private DNA dna;
	private int index;
	
	public GetGeneFromDNA(Cell cell) {
		this.cell = cell;
	}
	public ListIterator<Nucleotide> action(Object o) {
		for (int i = 0; i < 2; i++) {
			if (dna == null) {
				dna = cell.getDNA();
				index = 0;
			}
			for (; (index + 6) < dna.size(); index++) {
				if (isPromoter(index)) {
					index += 6;
					return dna.listIterator(index);
				}
			}
			dna = null;
		}
		throw new IllegalStateException("unable to find a promoter in the DNA");
	}
	private boolean isPromoter(int index) {
		for (int j = 0; j < 6; j++) {
			Nucleotide nucleotide = dna.get(index + j);
			if (!(nucleotide.getCode() == NucleicAcid.promoterCodes.charAt(j))) {
				return false; // i.e. NOT a promoter
			}
		}
		return true;
	}
	// Convenience method used for testing.
	public static DNA buildDNAFromString(String dnaStr) {
		DNA dna = new DNA();
		for (int i = 0; i < dnaStr.length(); i++) {
			char code = dnaStr.charAt(i); 
			switch (code) {
			case 'A':
				dna.add(new Adenine());
				break;
			case 'T':
				dna.add(new Thymine());
				break;
			case 'C':
				dna.add(new Cytosine());
				break;
			case 'G':
				dna.add(new Guanine());
				break;
			}
		}
		return dna;
	}
	public String getName() {
		return "GetGeneFromDNA";
	}
	public boolean matchCodon(Codon codon) {
		return codon.getFirst().getName().equals("Uracil") &&
		codon.getSecond().getName().equals("Uracil") &&
		codon.getThird().getName().equals("Guanine");
	}
}
