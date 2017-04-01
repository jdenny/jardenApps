package jarden.life.nucleicacid;


import java.util.ArrayList;
import java.util.List;

import jarden.life.CellResource;
import jarden.life.ChainResource;
import jarden.life.TargetResource;

/**
 * In our simplified view of life, a start codon (promoter) is only 3 base-pairs
 * (1 codon), and we are hijacking one of the triplets that in real life is used
 * as a stop. Also note that in real life, start and stop codons can appear in
 * DNA and RNA; in our version RNA doesn't currently include the stop codon.
 * In summary:       RNA    DNA
 *      start codon: UGA    TGA
 *      stop codon:  UAA    TAA
 */
public class Codon implements ChainResource, TargetResource {
    private final List<Nucleotide> triplet = new ArrayList<>(3);
    private Boolean isStart;
    private Boolean isStop;
    private int index = 0;
	
	public Codon(Nucleotide first, Nucleotide second, Nucleotide third) {
        triplet.add(first);
        triplet.add(second);
        triplet.add(third);
	}
	public Codon() {}
	public Nucleotide getFirst() {
        return (triplet.size() == 3) ? triplet.get(0) : null;
	}
	public Nucleotide getSecond() {
        return (triplet.size() == 3) ? triplet.get(1) : null;
	}
	public Nucleotide getThird() {
        return (triplet.size() == 3) ? triplet.get(2) : null;
	}
    public boolean isStart() {
        if (isStart == null && triplet.size() == 3) {
            // lazy evaluation:
            Nucleotide first = getFirst();
            isStart = (first instanceof Uracil || first instanceof Thymine) &&
                    getSecond() instanceof Guanine &&
                    getThird() instanceof Adenine;
        }
        return isStart == null ? false : isStart.booleanValue();
    }
	public boolean isStop() {
        if (isStop == null && triplet.size() == 3) {
            // lazy evaluation:
            Nucleotide first = getFirst();
            isStop = (first instanceof Uracil || first instanceof Thymine) &&
                    getSecond() instanceof Adenine &&
                    getThird() instanceof Adenine;
        }
        return isStop == null ? false : isStop.booleanValue();
	}
	public String toString() {
        StringBuilder builder = new StringBuilder('(');
        for (Nucleotide nucleotide: triplet) {
            builder.append(nucleotide);
            builder.append(",");
        }
        builder.append(')');
		return builder.toString();
	}

    @Override
    public String getName() {
        StringBuilder builder = new StringBuilder();
        for (Nucleotide nucleotide: triplet) {
            builder.append(nucleotide.getCode());
        }
        return builder.toString();
    }
    @Override
    public boolean hasNext() {
        if (index >= 3) {
            index = 0;
            return false;
        } else return true;
    }
    @Override
    public CellResource next() {
        return triplet.get(index++);
    }
    @Override
    public TargetResource getTargetResource() {
        return new Codon();
    }
    @Override
    public void add(CellResource resource) {
        triplet.add((Nucleotide) resource);
    }
}
