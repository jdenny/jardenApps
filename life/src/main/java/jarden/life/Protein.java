package jarden.life;

import jarden.life.aminoacid.AminoAcid;

import java.util.ArrayList;

public class Protein implements Runnable, CellResource {
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
            cell.logId("starting thread for protein " + this);
            thread.start();
        } else {
            cell.logId("not starting thread for protein " + this);
        }
    }
    public void stop() {
        cell.logId("Protein.stop(); thread=" + thread + " state=" + thread.getState());
        thread.interrupt();
    }

	public void run() {
		try {
            while (true) action(null);
        } catch (InterruptedException e) {
            cell.logId("protein.run() interrupted");
        }
	}
    // TODO: remove parameter to this method?
    private CellResource action(CellResource resource) throws InterruptedException {
        // if firstObject is a chain, then repeat until end of chain
        AminoAcid firstAminoAcid = aminoAcidList.get(0);
        CellResource currentResource = resource;
        do {
            for (AminoAcid aminoAcid: aminoAcidList) {
                if (Thread.interrupted()) {
                    // reset interrupt flag, so run() will exit
                    Thread.currentThread().interrupt();
                    return null;
                }
                currentResource = aminoAcid.action(currentResource);
            }
        } while (firstAminoAcid.hasMore());
        return currentResource;
    }

	public void add(AminoAcid aminoAcid) {
		aminoAcidList.add(aminoAcid);
        aminoAcid.setProtein(this);
	}
    public String getName() {
        return toString();
    }
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (AminoAcid aminoAcid: aminoAcidList) {
			buffer.append(aminoAcid.getName() + " ");
		}
		return buffer.toString();
	}
    /**
     * Should be called when protein is not running, to reset each aminoAcid
     */
    public void setCell(Cell cell) {
        this.cell = cell;
        for (AminoAcid aminoAcid: aminoAcidList) {
            aminoAcid.reset();
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
