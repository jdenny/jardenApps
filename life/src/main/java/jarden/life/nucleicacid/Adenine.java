package jarden.life.nucleicacid;

public class Adenine implements Nucleotide {
    @Override
    public boolean dnaMatch(Nucleotide otherNucleotide) {
        return otherNucleotide instanceof Thymine;
    }
    @Override
    public boolean rnaMatch(Nucleotide otherNucleotide) {
        return otherNucleotide instanceof Uracil;
    }
    public char getCode() {
		return 'A';
	}
	public String toString() {
		return "Adenine";
	}
	public String getName() {
		return "Adenine";
	}
}
