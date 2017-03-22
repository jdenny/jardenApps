package jarden.life.nucleicacid;


import java.util.ArrayList;
import java.util.List;

import jarden.life.CellResource;
import jarden.life.ChainResource;
import jarden.life.Protein;
import jarden.life.TargetResource;

public class RNA implements ChainResource, TargetResource {
    private List<Codon> codonList = new ArrayList<>();
    private Protein newProtein;
    private int index = 0;

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
    @Override
    public boolean hasNext() {
        return index < codonList.size();
    }
    @Override
    public Codon next() {
        return codonList.get(index++);
    }

    @Override
    public void add(CellResource resource) {
        addCodon((Codon) resource);
    }
}
