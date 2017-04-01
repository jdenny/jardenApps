package jarden.life;

import jarden.life.aminoacid.AminoAcid;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Protein implements Runnable, CellResource, TargetResource {
    private Cell cell; // the cell this protein belongs to
	private List<AminoAcid> aminoAcidList = new ArrayList<>();
    private Regulator regulator;
    private int aminoAcidIndex;
    private int hashCode;
    private Thread thread = null;
    private String state; // for monitoring; put in LifeFX
    private ArrayList<AminoAcid> aminoAcidBodyList;
    private List<ArrayList<AminoAcid>> listOfBodies = new ArrayList<>();
    private boolean isForDna;

    /**
     * Used for debugging; set false if this protein should
     * not run; used in Cell.addProtein().
     */
    public boolean activate = true;
    private Future future;

    public Protein(Cell cell) {
        this.cell = cell;
    }
    public Cell getCell() {
        return this.cell;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) { this.state = state; }
    public void start(ThreadPoolExecutor threadPoolExecutor) {
        if (regulator.runBelowTarget()) {
            cell.logId("starting thread for protein " + this);
            regulator.incrementRunCt();
            future = threadPoolExecutor.submit(this);
        } else {
            cell.logId("not starting thread for protein " + this);
        }
    }
    public void stop() {
        cell.logId("Protein.stop(); future=" + future);
        if (future != null) {
            future.cancel(true);
            regulator.decrementRunCt();
            try {
                future.get(600, TimeUnit.MILLISECONDS);
                cell.logId("Protein " + this + " finished");
            } catch (CancellationException | InterruptedException e) {
                cell.logId("Protein " + this + " cancelled okay");
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }
    }
	public void run() {
        this.thread = Thread.currentThread();
		try {
            while (true) action(null);
        } catch (InterruptedException e) {
            cell.logId("protein.run() interrupted");
        }
	}
	public Thread getThread() {
        return thread;
    }
    @Override
    public void add(CellResource resource) {
        addAminoAcid((AminoAcid) resource);
    }

    public void setIsForDna(boolean isForDna) {
        this.isForDna = isForDna;
    }
    public boolean isForDna() {
        return isForDna;
    }

    private enum RnaMode {
        data, code, body;
    }
    private CellResource action(CellResource resource) throws InterruptedException {
        CellResource currentResource = resource;
        RnaMode rnaMode = RnaMode.code;
        int aaSize = aminoAcidList.size();
        for (aminoAcidIndex = 0; aminoAcidIndex < aaSize; aminoAcidIndex++) {
            if (Thread.interrupted()) {
                throw new InterruptedException(
                        "Thread.interrupted detected in Protein.action()");
            }
            AminoAcid aminoAcid = aminoAcidList.get(aminoAcidIndex);
            if (aminoAcid.isData()) {
                rnaMode = RnaMode.data;
                currentResource = null;
            } else if (aminoAcid.isCode()) {
                rnaMode = RnaMode.code;
            } else if (aminoAcid.isBody()) {
                rnaMode = RnaMode.body;
                aminoAcidBodyList = new ArrayList<>();
                listOfBodies.add(aminoAcidBodyList);
            } else if (rnaMode == RnaMode.code) {
                currentResource = aminoAcid.action(currentResource);
            } else if (rnaMode == RnaMode.body) {
                aminoAcidBodyList.add(aminoAcid);
            }
        }
        return currentResource;
    }
    public AminoAcid getAminoAcid(int relativeIndex) {
        return aminoAcidList.get(aminoAcidIndex + relativeIndex);
    }
    public void addAminoAcid(AminoAcid aminoAcid) {
		aminoAcidList.add(aminoAcid);
        aminoAcid.setProtein(this);
	}
    public String getName() {
        return toString();
    }
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (AminoAcid aminoAcid: aminoAcidList) {
			buffer.append(aminoAcid.getShortName() + " ");
		}
		return buffer.toString();
	}
    /**
     * Should be called when protein is not running, to reset each aminoAcid
     */
    public void setCell(Cell cell, Regulator regulator) {
        this.cell = cell;
        this.regulator = regulator;
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
    public List<AminoAcid> getAminoAcidList() {
        return aminoAcidList;
    }

    public Future getFuture() {
        return future;
    }

    public Regulator getRegulator() {
        return regulator;
    }

    public void setRegulator(Regulator regulator) {
        this.regulator = regulator;
    }

    public List<AminoAcid> getBody() {
        int bodiesSize = listOfBodies.size();
        if (bodiesSize == 0) return null;
        return listOfBodies.remove(bodiesSize - 1);
    }
}
