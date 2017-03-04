package jarden.life.nucleicacid;


import jarden.life.CellResource;

public class Codon implements CellResource {
	private Nucleotide first;
	private Nucleotide second;
	private Nucleotide third;
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

	public boolean isStop() {
        if (isStop == null) {
            // lazy evaluation:
            isStop = (first instanceof Uracil || first instanceof Thymine) &&
                         ( (second instanceof Adenine &&
                         (third instanceof Adenine || third instanceof Guanine)) ||
                         (second instanceof Guanine && third instanceof Adenine) );
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
