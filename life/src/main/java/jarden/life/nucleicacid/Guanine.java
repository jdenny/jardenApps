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
    @Override
	public char getCode() {
		return 'G';
	}
    @Override
	public String getName() {
		return "Guanine";
	}
    @Override
	public String toString() {
		return "Guanine";
	}
    @Override
    public int getIndex() {
        return 2;
    }
}
