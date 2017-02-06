package jarden.life.nucleicacid;


import java.util.ArrayList;

public class RNA extends ArrayList<Codon> {
	private static final long serialVersionUID = 1L;

	public String toString() {
		StringBuffer buffer = new StringBuffer("RNA: ");
		for (Codon codon: this) {
			buffer.append(codon + "  ");
		}
		return buffer.toString();
	}


}
