package jarden.life.nucleicacid;

public class Thymine implements Nucleotide {
    @Override
    public boolean dnaMatch(Nucleotide nucleotide) {
        return nucleotide instanceof Adenine;
    }
    @Override
    public boolean rnaMatch(Nucleotide nucleotide) {
        return false; // Thymine not used in RNA
    }
	public char getCode() {
		return 'T';
	}
	public String toString() {
		return "Thymine";
	}
	public String getName() {
		return "Thymine";
	}
}
