package jarden.life.nucleicacid;

public class Guanine implements Nucleotide {
    @Override
    public boolean dnaMatch(Nucleotide otherNucleotide) {
        return otherNucleotide instanceof Cytosine;
    }
    @Override
    public boolean rnaMatch(Nucleotide otherNucleotide) {
        return otherNucleotide instanceof Cytosine;
    }
	public char getCode() {
		return 'G';
	}
	public String getName() {
		return "Guanine";
	}
	public String toString() {
		return "Guanine";
	}
}
