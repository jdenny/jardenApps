package jarden.life.nucleicacid;

public class Uracil implements Nucleotide {
    @Override
    public boolean dnaMatch(Nucleotide otherNucleotide) {
        return false; // Uracil not used in DNA
    }
    @Override
    public boolean rnaMatch(Nucleotide otherNucleotide) {
        return otherNucleotide instanceof Adenine;
    }
	public char getCode() {
		return 'U';
	}
	public String toString() {
		return "Uracil";
	}
	public String getName() {
		return "Uracil";
	}
}
