package jarden.life;

import jarden.life.aminoacid.AminoAcid;

import java.util.ArrayList;

public class Protein implements Runnable {
	private ArrayList<AminoAcid> aminoAcidList = new ArrayList<>();

	public Protein() {
	}
	public void run() {
		while (true) action(null);
	}
	public void add(AminoAcid aminoAcid) {
		aminoAcidList.add(aminoAcid);
	}
	public String toString() {
		StringBuffer buffer = new StringBuffer("Protein: ");
		for (AminoAcid aminoAcid: aminoAcidList) {
			buffer.append(aminoAcid.getName() + "  ");
		}
		return buffer.toString();
	}
	/*
	 * object is initially null, but can be a real object
	 * returned from action of one aminoAcid to be passed
	 * on to next aminoAcid.
	 */
	public Object action(Object object) {
        // if firstObject is a chain, then repeat until end of chain
        AminoAcid firstAminoAcid = aminoAcidList.get(0);
        boolean isChain = firstAminoAcid.isChain();
        Object currentObject = object;
        do {
            for (AminoAcid aminoAcid: aminoAcidList) {
                currentObject = aminoAcid.action(currentObject);
            }
        } while (isChain && firstAminoAcid.hasMore());
		return currentObject;
	}
}
