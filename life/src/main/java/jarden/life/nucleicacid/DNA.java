package jarden.life.nucleicacid;


import java.util.ArrayList;

import jarden.life.CellResource;

public class DNA extends ArrayList<Nucleotide> implements CellResource {
	private static final long serialVersionUID = 1L;

	public String toString() {
		StringBuffer buffer = new StringBuffer("DNA: ");
		for (Nucleotide nucleotide: this) {
			buffer.append(nucleotide + "  ");
		}
		return buffer.toString();
	}
    public String dnaToString() {
        StringBuilder builder = new StringBuilder();
        for (Nucleotide nucleotide: this) {
            builder.append(nucleotide.getCode());
        }
        return builder.toString();
    }
    @Override
    public String getName() {
        return this.toString();
    }
}
