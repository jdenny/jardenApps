package jarden.life;

import jarden.life.aminoacid.AminoAcid;

import java.util.ArrayList;

public class Protein implements Runnable {
    private String name;
    private String type;
	private ArrayList<AminoAcid> aminoAcidList = new ArrayList<>();
    private Object objectPassedToAction;
    private boolean stopping;
    private Thread thread;
    private int hashCode;

    public void stopAction() {
        this.stopping = true;
        thread.interrupt();
    }
	public void run() {
		while (!stopping) action(null);
	}
    public boolean isRunning() {
        return thread != null && thread.isAlive();
    }
	public void add(AminoAcid aminoAcid) {
		aminoAcidList.add(aminoAcid);
	}
	public String toString() {
		StringBuffer buffer = new StringBuffer("Protein:");
		for (AminoAcid aminoAcid: aminoAcidList) {
			buffer.append(" " + aminoAcid.getName());
		}
		return buffer.toString();
	}
	/*
	 * object is initially null, but can be a real object
	 * returned from action of one aminoAcid to be passed
	 * on to next aminoAcid.
	 */
	/*!!
    public Object action(Object object) {
        AminoAcid firstAminoAcid = aminoAcidList.get(0);
        if (firstAminoAcid.keepRunning()) {
            this.objectPassedToAction = object;
            // TODO: create a ThreadGroup for each cell
            thread = new Thread(this);
            thread.start();
            MasterDesigner.print(this + " running in its own thread");
            return null;
        } else {
            return action2(object);
        }
    }
    */
    // TODO: remove parameter to this method?
    public Object action(Object object) {
        // if firstObject is a chain, then repeat until end of chain
        AminoAcid firstAminoAcid = aminoAcidList.get(0);
        boolean isChain = firstAminoAcid.isChain();
        Object currentObject = object;
        do {
            for (AminoAcid aminoAcid: aminoAcidList) {
                currentObject = aminoAcid.action(currentObject);
                if (stopping) break;
            }
        } while (isChain && firstAminoAcid.hasMore() && !stopping);
		return currentObject;
	}

    public void setCell(Cell cell) {
        for (AminoAcid aminoAcid: this.aminoAcidList) {
            aminoAcid.setCell(cell);
        }
    }
    @Override
    public boolean equals(Object any) {
        if (any instanceof Protein) {
            return any.toString().equals(this.toString());
        } else return false;
    }
    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            // lazy evaluation:
            this.hashCode = this.toString().hashCode();
        }
        return this.hashCode;
    }
}
