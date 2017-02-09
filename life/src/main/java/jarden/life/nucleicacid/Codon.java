package jarden.life.nucleicacid;


public class Codon {
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
        /*!!
		char f = first.getCode();
		char s = second.getCode();
		char t = third.getCode();
		return (f == 'U' || f == 'T') && (
			(s == 'A' && (t == 'A' || t == 'G'))
			|| (s == 'G' && t == 'A')
			);
		 */
	}
	public String toString() {
		return "(" + first + "," + second + "," + third + ")";
	}
}
