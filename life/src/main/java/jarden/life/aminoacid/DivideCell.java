package jarden.life.aminoacid;

import java.util.List;

import jarden.life.Cell;
import jarden.life.MasterDesigner;
import jarden.life.OnNewCellListener;
import jarden.life.Protein;
import jarden.life.nucleicacid.Adenine;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.Cytosine;
import jarden.life.nucleicacid.DNA;
import jarden.life.nucleicacid.Uracil;

/**
 * Created by john.denny@gmail.com on 13/02/2017.
 *
 * Create new cell which is identical to this cell.
 * Create new cell; add copy of own DNA; run polymerase & ribosome to
 * create copies of all own proteins, and add these to new cell.
 * @return identical copy of this cell
 */

public class DivideCell extends AminoAcid /*!!implements CellReadyToSplitListener*/ {

    /*
    Proposed design:
    when there are enough proteins to divide, stop all proteins in this cell
    (apart from DivideCell!), wait for them all to stop, then run code
    currently in splitCell(); what really happens?
     */
    public DivideCell(Cell cell) {
        super(cell);
    }

    public boolean activateOnCreate() {
        Cell cell = getCell();
        if (cell.isDivideCellRunning()) return false;
        else {
            cell.setDivideCellRunning(true);
            return true;
        }
    }

    public Object action(Object object) {
        Cell cell = getCell();
        List<Protein> proteinList = cell.getProteinList();
        int geneSize = cell.getGeneSize();
        synchronized (proteinList) {
            while (true) {
                int proteinSize = proteinList.size();
                if (proteinSize >= (geneSize * 2)) {
                    // for all proteins in proteinList > geneSize:
                    //    stop protein
                    //    when stopped, move protein new new cell
                    // TODO: use life to copy DNA
                    String dnaStr = cell.getDNA().dnaToString();
                    DNA daughterDNA = cell.buildDNAFromString(dnaStr);
                    Cell daughterCell = new Cell(daughterDNA);
                    daughterCell.setGeneration(cell.getGeneration() + 1);
                    int newProteinCount = proteinList.size();
                    for (int i = geneSize; i < newProteinCount; i++) {
                        Protein protein = proteinList.remove(geneSize);
                        Thread proteinThread = protein.getThread();
                        if (proteinThread != null) {
                            protein.stop();
                            MasterDesigner.print(Thread.currentThread().getName() +
                                    " divideCell requested stop to protein " + protein);
                            try {
                                proteinThread.join();
                                MasterDesigner.print(Thread.currentThread().getName() +
                                        " divideCell protein stopped: " + protein);
                            } catch (InterruptedException e) {
                                MasterDesigner.print(Thread.currentThread().getName() +
                                    " divideCell interrupted while waiting for protein to stop ");
                                Thread.currentThread().interrupt();
                                return null;
                            }
                        } else {
                            MasterDesigner.print(Thread.currentThread().getName() +
                                    " divideCell detected no thread for protein " + protein);

                        }
                        protein.setCell(daughterCell);
                        daughterCell.addProtein(protein); // this should start the thread
                    }
                    OnNewCellListener onNewCellListener = cell.getOnNewCellListener();
                    if (onNewCellListener != null) {
                        daughterCell.setOnNewCellListener(onNewCellListener);
                        onNewCellListener.onNewCell(daughterCell);
                    }
                    return null;
                }
                MasterDesigner.print(Thread.currentThread().getName() +
                        " cell divide waiting for " +
                        ((geneSize * 2) - proteinSize) +
                        " more proteins");
                try { proteinList.wait(); }
                catch(InterruptedException e) {
                    MasterDesigner.print(Thread.currentThread().getName() +
                            " interrupted while waiting for proteins");
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
        }
    }
    public String getName() {
        return "DivideCell";
    }
    public boolean matchCodon(Codon codon) {
        return codon.getFirst() instanceof Uracil &&
                codon.getSecond() instanceof Adenine &&
                codon.getThird() instanceof Cytosine;
    }
}
