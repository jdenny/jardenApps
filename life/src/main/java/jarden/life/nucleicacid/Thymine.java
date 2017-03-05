package jarden.life.nucleicacid;

public class Thymine implements Nucleotide {
    @Override
    public boolean dnaMatch(Nucleotide nucleotide) {
        return nucleotide instanceof Thymine; // Adenine;
    }
    @Override
    public boolean rnaMatch(Nucleotide nucleotide) {
        return false; // Thymine not used in RNA
    }
    @Override
    public char getCode() {
		return 'T';
	}
    @Override
	public String toString() {
		return "Thymine";
	}
    @Override
	public String getName() {
		return "Thymine";
	}
    @Override
    public int getIndex() {
        return 3;
    }
}
