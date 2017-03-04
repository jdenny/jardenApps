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
    @Override
    public char getCode() {
		return 'A';
	}
    @Override
	public String toString() {
		return "Adenine";
	}
    @Override
	public String getName() {
		return "Adenine";
	}
    @Override
    public int getIndex() {
        return 0;
    }
}
