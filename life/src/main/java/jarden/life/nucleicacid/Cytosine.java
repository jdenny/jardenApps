package jarden.life.nucleicacid;

public class Cytosine implements Nucleotide {
    @Override
    public boolean dnaMatch(Nucleotide otherNucleotide) {
        return otherNucleotide instanceof Cytosine; // Guanine;
    }
    @Override
    public boolean rnaMatch(Nucleotide otherNucleotide) {
        return otherNucleotide instanceof Cytosine; // Guanine;
    }
    @Override
	public char getCode() {
		return 'C';
	}
    @Override
	public String toString() {
		return "Cytosine";
	}
    @Override
	public String getName() {
		return "Cytosine";
	}
    @Override
    public int getIndex() {
        return 1;
    }
}
