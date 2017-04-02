package jarden.life;

import jarden.life.aminoacid.AminoAcid;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Protein implements Runnable, CellResource, TargetResource {
    private enum RnaMode {
        data, code, body;
    }
    private Cell cell; // the cell this protein belongs to
	private List<AminoAcid> aminoAcidList = new ArrayList<>();
    private List<AminoAcid> dataList = new LinkedList<>();
    private List<AminoAcid> codeList = new ArrayList<>();
    private List<ArrayList<AminoAcid>> listOfBodies = new ArrayList<>();
    // current body list; used in prepare stage:
    private ArrayList<AminoAcid> aminoAcidBodyList;
    private Regulator regulator;
    private int hashCode;
    private Thread thread = null;
    private String state; // for monitoring; put in LifeFX
    private boolean isForDna;
    private boolean prepared = false; // in real life, "folded"

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

    private CellResource action(CellResource resource) throws InterruptedException {
        // new bits
        if (!prepared) {
            RnaMode rnaMode = RnaMode.code;
            for (AminoAcid aminoAcid: aminoAcidList) {
                if (Thread.interrupted()) {
                    throw new InterruptedException(
                            "Thread.interrupted detected in Protein.action()");
                }
                if (aminoAcid.isData()) {
                    rnaMode = RnaMode.data;
                    // TODO: this may come back to bite us one day!
                    // used to turn off other way of passing data
                    // to Tryptophan; not relevant with current style
                    // of passing data to Tryptophan
                    // currentResource = null;
                } else if (aminoAcid.isCode()) {
                    rnaMode = RnaMode.code;
                } else if (aminoAcid.isBody()) {
                    rnaMode = RnaMode.body;
                    aminoAcidBodyList = new ArrayList<>();
                    listOfBodies.add(aminoAcidBodyList);
                } else if (rnaMode == RnaMode.code) {
                    codeList.add(aminoAcid);
                } else if (rnaMode == RnaMode.body) {
                    aminoAcidBodyList.add(aminoAcid);
                } else if (rnaMode == RnaMode.data) {
                    dataList.add(aminoAcid);
                }
            }
            prepared = true;
        }
        CellResource currentResource = resource;
        for (AminoAcid aminoAcid: codeList) {
            currentResource = aminoAcid.action(currentResource);
        }
        return currentResource;
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

    /*
    Last in, first out, to cater for nested bodies.
     */
    public List<AminoAcid> getBody() {
        int bodiesSize = listOfBodies.size();
        if (bodiesSize == 0) return null;
        return listOfBodies.remove(bodiesSize - 1);
    }
    public AminoAcid getData() {
        if (dataList.size() == 0) return null;
        return dataList.remove(0);
    }
}
