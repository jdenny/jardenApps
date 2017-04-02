package jarden.life.nucleicacid;

import java.util.ArrayList;
import java.util.List;

import jarden.life.Cell;
import jarden.life.CellResource;
import jarden.life.ChainResource;
import jarden.life.TargetResource;

public class DNA implements ChainResource, TargetResource {
    private List<Nucleotide> strand1 = new ArrayList<>(); // master
    private List<Nucleotide> strand2 = new ArrayList<>(); // DNA messenger template
    private int sourceIndex = 0;
    private int targetIndex = 0;
    /*
    saved as instance variable, so can be copied to targetResource
    and used in TargetResource.add(nucleotide);
      */
    private int dnaSize;

    public void add(Nucleotide n1, Nucleotide n2) {
        if (n1.dnaMatch(n2)) {
            strand1.add(n1);
            strand2.add(n2);
        } else {
            Cell.throwError2("DNA.add(" + n1 + ", " + n2 + ") - no match");
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

    @Override
    public boolean hasNext() {
        if (sourceIndex >= strand1.size() * 2) {
            sourceIndex = 0;
            return false;
        } else return true;
    }
    @Override
    public Nucleotide next() {
        int index = sourceIndex++;
        int dnaSize = strand1.size();
        if (index < dnaSize) {
            return strand1.get(index);
        } else {
            return strand2.get(index - dnaSize);
        }
    }
    @Override
    public TargetResource getTargetResource() {
        DNA dna = new DNA();
        dna.dnaSize = this.strand1.size();
        return dna;
    }
    @Override
    public void add(CellResource _nucleotide) {
        Nucleotide nucleotide = (Nucleotide) _nucleotide;
        int index = targetIndex++;
        if (index < dnaSize) {
            strand2.add(nucleotide);
        } else {
            strand1.add(nucleotide);
        }
    }
}
