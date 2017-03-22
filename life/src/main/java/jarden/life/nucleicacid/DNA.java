package jarden.life.nucleicacid;


import java.util.ArrayList;
import java.util.List;

import jarden.life.CellResource;

public class DNA implements CellResource {
    private List<Nucleotide> strand1 = new ArrayList<>(); // master
    private List<Nucleotide> strand2 = new ArrayList<>(); // DNA messenger template

    public void add(Nucleotide n1, Nucleotide n2) {
        if (n1.dnaMatch(n2)) {
            strand1.add(n1);
            strand2.add(n2);
        } else {
            throw new IllegalArgumentException("DNA.addAminoAcid(" + n1 + ", " + n2 + ") - no match");
        }
    }
    public Nucleotide getFromTemplate(int index) {
        return strand2.get(index);
    }
    public Nucleotide getFromMaster(int index) {
        return strand1.get(index);
    }
    public int size() {
        return strand1.size();
    }
	public String toString() {
		StringBuffer buffer = new StringBuffer("DNA: ");
		for (Nucleotide nucleotide: strand1) {
			buffer.append(nucleotide + "  ");
		}
		return buffer.toString();
	}
    public String dnaToString() {
        StringBuilder builder = new StringBuilder();
        for (Nucleotide nucleotide: strand1) {
            builder.append(nucleotide.getCode());
        }
        return builder.toString();
    }
    @Override
    public String getName() {
        return this.toString();
    }
    public List<Nucleotide> getStrand1() {
        return strand1;
    }
    public List<Nucleotide> getStrand2() {
        return strand2;
    }
    public void addToStrand1(Nucleotide nucleotide) {
        strand1.add(nucleotide);
    }
    public void addToStrand2(Nucleotide nucleotide) {
        strand2.add(nucleotide);
    }
}
