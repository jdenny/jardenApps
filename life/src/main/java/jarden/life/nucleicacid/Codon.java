package jarden.life.nucleicacid;


public class Codon {
	private Nucleotide first;
	private Nucleotide second;
	private Nucleotide third;
	
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
	/*
	 * @todo probably need to separate RNA stop from DNA stop.
	 * Perhaps this method could be part of GetAminoAcidFromCodon?
	 */
	public boolean isStop() {
        // TODO: replace this with
        // return first instanceof Uracil; // etc
		char f = first.getCode();
		char s = second.getCode();
		char t = third.getCode();
		return (f == 'U' || f == 'T') && (
			(s == 'A' && (t == 'A' || t == 'G'))
			|| (s == 'G' && t == 'A')
			);
	}
	public String toString() {
		return "(" + first + "," + second + "," + third + ")";
	}
}
