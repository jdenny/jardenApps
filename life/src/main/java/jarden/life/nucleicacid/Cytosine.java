package jarden.life.nucleicacid;

public class Cytosine implements Nucleotide {
    @Override
    public boolean dnaMatch(Nucleotide otherNucleotide) {
        return otherNucleotide instanceof Guanine;
    }
    @Override
    public boolean rnaMatch(Nucleotide otherNucleotide) {
        return otherNucleotide instanceof Guanine;
    }
	public char getCode() {
		return 'C';
	}
	public String toString() {
		return "Cytosine";
	}
	public String getName() {
		return "Cytosine";
	}
}
