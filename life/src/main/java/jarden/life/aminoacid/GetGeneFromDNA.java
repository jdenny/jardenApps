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
import jarden.life.nucleicacid.Uracil;

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
	private DNA dna;
	private int index;
	
	public GetGeneFromDNA(Cell cell) {
        super(cell);
	}
    @Override
	public ListIterator<Nucleotide> action(Object o) {
		//!! for (int i = 0; i < 2; i++) {
			if (dna == null) {
				dna = getCell().getDNA();
				index = 0;
			}
            if (getNextPromoterIndex() >= 0) {
                index += 6;
                return dna.listIterator(index);
            }
            /*!!
			for (; (index + 6) < dna.size(); index+=3) {
				if (isPromoter(index)) {
					index += 6;
					return dna.listIterator(index);
				}
			}
			*/
			//!! dna = null;
            index = 0;
            return null;
		//!! }
		//!! throw new IllegalStateException("unable to find a promoter in the DNA");
	}
    private int getNextPromoterIndex() {
        for (; (index + 6) < dna.size(); index+=3) {
            if (isPromoter(index)) {
                return index;
            }
        }
        return -1;
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
    @Override
	public String getName() {
		return "GetGeneFromDNA";
	}
    @Override
	public boolean matchCodon(Codon codon) {
        return codon.getFirst() instanceof Uracil &&
                codon.getSecond() instanceof Uracil &&
                codon.getThird() instanceof Guanine;
    }
    @Override
    public boolean isChain() { return true; }
    @Override
    public boolean hasMore() {
        return getNextPromoterIndex() >= 0;
    }
}
