package jarden.life;

import jarden.life.aminoacid.AminoAcid;

import java.util.ArrayList;

public class Protein implements Runnable {
    private Cell cell; // the cell this protein belongs to
    private String name;
    private String type;
	private ArrayList<AminoAcid> aminoAcidList = new ArrayList<>();
    private Thread thread;
    private int hashCode;
    private String state;

    public Protein(Cell cell) {
        this.cell = cell;
    }
    public Cell getCell() {
        return this.cell;
    }
    public void setState(String state) { this.state = state; }
    public void start() {
        // call method on 1st aminoAcid to see if it wants to start;
        // nearly always will say yes, but for example DivideCell will
        // say no if there is another DivideCell already running
        AminoAcid firstAminoAcid = aminoAcidList.get(0);
        if (firstAminoAcid.activateOnCreate()) {
            this.thread = new Thread(this);
            MasterDesigner.print(cell + "; " + thread.getName() +
                    " starting thread for protein " + this);
            thread.start();
        } else {
            MasterDesigner.print(cell + "; " +
                    " not starting thread for protein " + this);
        }
    }
    public void stop() {
        thread.interrupt();
    }

	public void run() {
		while (!Thread.interrupted()) action(null);
	}
    // TODO: remove parameter to this method?
    public Object action(Object object) {
        // if firstObject is a chain, then repeat until end of chain
        AminoAcid firstAminoAcid = aminoAcidList.get(0);
        Object currentObject = object;
        do {
            for (AminoAcid aminoAcid: aminoAcidList) {
                if (Thread.interrupted()) {
                    // reset interrupt flag, so run() will exit
                    Thread.currentThread().interrupt();
                    return null;
                }
                currentObject = aminoAcid.action(currentObject);
            }
        } while (firstAminoAcid.hasMore());
        return currentObject;
    }

	public void add(AminoAcid aminoAcid) {
		aminoAcidList.add(aminoAcid);
        aminoAcid.setProtein(this);
	}
	public String toString() {
		StringBuffer buffer = new StringBuffer("Protein:");
		for (AminoAcid aminoAcid: aminoAcidList) {
			buffer.append(" " + aminoAcid.getName());
		}
		return buffer.toString();
	}

    public void setCell(Cell cell) {
        this.cell = cell;
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

    public Thread getThread() {
        return thread;
    }

    public String getStatus() {
        return "running=" + (thread != null && thread.isAlive()) +
                "; state=" + state;
    }

    public ArrayList<AminoAcid> getAminoAcidList() {
        return aminoAcidList;
    }
}
