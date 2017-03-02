package jarden.life.nucleicacid;


import java.util.ArrayList;

import jarden.life.CellResource;

public class RNA extends ArrayList<Codon> implements CellResource {
	private static final long serialVersionUID = 1L;

	public String toString() {
		StringBuffer buffer = new StringBuffer("RNA: ");
		for (Codon codon: this) {
			buffer.append(codon + "  ");
		}
		return buffer.toString();
	}
    public String getName() {
        return this.toString();
    }
}
