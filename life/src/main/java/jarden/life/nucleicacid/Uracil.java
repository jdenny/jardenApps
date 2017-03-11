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
    @Override
	public char getCode() {
		return 'U';
	}
    @Override
	public String toString() {
		return "Uracil";
	}
    @Override
	public String getName() {
		return "Uracil";
	}
    @Override
    public int getIndex() {
        return 4;
    }
}
