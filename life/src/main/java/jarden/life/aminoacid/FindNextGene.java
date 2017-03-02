package jarden.life.aminoacid;

import java.util.ListIterator;

import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.DNA;
import jarden.life.nucleicacid.Guanine;
import jarden.life.nucleicacid.Nucleotide;
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
public class FindNextGene extends AminoAcid {
	private DNA dna;
	private int index;
    private int currentStop = 0;
	
    @Override
	public ListIterator<Nucleotide> action(Object o) {
        /*
        Keep dnaIndex in cell, along with dna;
        action()
            put lock on dnaIndex;
            get dna & dnaIndex from cell;
            index = next promoter >= index
            if no promoter:
                index = 0
                index = next promoter >= index
                if no promoter: throw exception
            find next stop
            set dnaIndex to position after stop
            release lock on dnaIndex
            start building RNA!

        This is silly, as it's duplicating work done in the 2nd amino acid of this
        protein; current suggestion: aminoAcids belong to a protein, that in turn
        belongs to a cell; the aminoAcids can then communicate with the protein,
        so in the case of a chain, the protein can keep a track of the current index

        index = next promoter after stop
        if no promoter:
            stop = 0
            index = next promoter after stop
            if no promoter: throw exception
        stop = get stop after index
        if no stop: throw exception
        return listIterator(index)
         */
        if (dna == null) {
            dna = getCell().getDNA();
            index = 0;
            currentStop = 0;
        }
        if (getNextPromoterIndex() < 0) {
            currentStop = 0;
            if (getNextPromoterIndex() < 0) {
                throw new IllegalStateException("DNA contains no promoter");
            }
        }
        index += 6; // i.e. first nucleotide after promoter
        if (getNextStop() < 0) {
            throw new IllegalStateException("DNA gene has no terminator");
        }
        return dna.listIterator(index);
	}
    private int getNextPromoterIndex() {
        index = currentStop;
        for (; (index + 6) <= dna.size(); index+=3) {
            if (isPromoter(index)) {
                return index;
            }
        }
        return -1;
    }
	private boolean isPromoter(int index) {
		for (int j = 0; j < 6; j++) {
			Nucleotide nucleotide = dna.get(index + j);
			if (!(nucleotide.getCode() == Nucleotide.promoterCode.charAt(j))) {
				return false; // i.e. NOT a promoter
			}
		}
		return true;
	}
    private int getNextStop() {
        currentStop = index;
        for (; (currentStop + 3) <= dna.size(); currentStop+=3) {
            if (isStop(currentStop)) {
                currentStop += 3; // i.e. first nucleotide after stop
                return currentStop;
            }
        }
        return -1;
    }
    private boolean isStop(int stopIndex) {
        Codon codon = new Codon(dna.get(stopIndex), dna.get(stopIndex + 1),
                dna.get(stopIndex + 2));
        return codon.isStop();
    }
    @Override
	public String getName() {
		return "FindNextGene";
	}
    @Override
	public boolean matchCodon(Codon codon) {
        return codon.getFirst() instanceof Uracil &&
                codon.getSecond() instanceof Uracil &&
                codon.getThird() instanceof Guanine;
    }
    @Override
    public boolean hasMore() {
        return getNextPromoterIndex() >= 0;
    }
    @Override
    public void reset() {
        this.dna = null;
    }
}
