package jarden.life.nucleicacid;


import java.util.ArrayList;
import java.util.List;

import jarden.life.CellResource;
import jarden.life.Protein;

public class RNA implements CellResource {
    private List<Codon> codonList = new ArrayList<>();
    private Protein newProtein;

    public Protein getNewProtein() {
        return newProtein;
    }
    public void setNewProtein(Protein newProtein) {
        this.newProtein = newProtein;
    }
    public String toString() {
		StringBuffer buffer = new StringBuffer("RNA: ");
		for (Codon codon: codonList) {
			buffer.append(codon + "  ");
		}
		return buffer.toString();
	}
    public String getName() {
        return this.toString();
    }

    public Codon getCodon(int index) {
        return codonList.get(index);
    }
    public void addCodon(Codon codon) {
        codonList.add(codon);
    }
}
