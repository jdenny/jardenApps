package jarden.life.nucleicacid;


import jarden.life.CellResource;

/**
 * In our simplified view of life, a start codon (promoter) is only 3 base-pairs
 * (1 codon), and we are hijacking one of the triplets that in real life is used
 * as a stop. Also note that in real life, start and stop codons can appear in
 * DNA and RNA; in our version RNA doesn't currently include the stop codon.
 * In summary:       RNA    DNA
 *      start codon: UGA    TGA
 *      stop codon:  UAA    TAA
 */
public class Codon implements CellResource {
	private Nucleotide first;
	private Nucleotide second;
	private Nucleotide third;
    private Boolean isStart;
    private Boolean isStop;
	
	public Codon(Nucleotide first, Nucleotide second, Nucleotide third) {
		this.first = first;
		this.second = second;
		this.third = third;
	}
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
}
