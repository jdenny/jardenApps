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
    // TODO: when we've worked out how to have loop
    // within a loop, replace first, second, third with triplet,
    // so we can convert a dnaCodon into an rnaCodon.
    private List<Nucleotide> triplet = new ArrayList<>(3);
	private Nucleotide first;
	private Nucleotide second;
	private Nucleotide third;
    private Boolean isStart;
    private Boolean isStop;
    private int index = 0;
	
	public Codon(Nucleotide first, Nucleotide second, Nucleotide third) {
		this.first = first;
		this.second = second;
		this.third = third;
	}
	public Codon() {}
	public Nucleotide getFirst() {
		return first;
	}
	public Nucleotide getSecond() {
		return second;
	}
	public Nucleotide getThird() {
		return third;
	}

    public boolean isStart() {
        if (isStart == null) {
            // lazy evaluation:
            isStart = (first instanceof Uracil || first instanceof Thymine) &&
                    second instanceof Guanine && third instanceof Adenine;
        }
        return isStart;
    }
	public boolean isStop() {
        if (isStop == null) {
            // lazy evaluation:
            isStop = (first instanceof Uracil || first instanceof Thymine) &&
                         second instanceof Adenine && third instanceof Adenine;
        }
        return isStop;
	}
	public String toString() {
		return "(" + first + "," + second + "," + third + ")";
	}

    @Override
    public String getName() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(first.getCode());
        stringBuilder.append(second.getCode());
        stringBuilder.append(third.getCode());
        return stringBuilder.toString();
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
        return index == 0 ? first : index == 1 ? second : third;
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
