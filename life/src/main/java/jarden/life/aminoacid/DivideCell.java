package jarden.life.aminoacid;

import java.util.List;

import jarden.life.Cell;
import jarden.life.MasterDesigner;
import jarden.life.CellListener;
import jarden.life.Protein;
import jarden.life.nucleicacid.Adenine;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.Cytosine;
import jarden.life.nucleicacid.DNA;
import jarden.life.nucleicacid.Uracil;

/**
 * When enough proteins, create a new cell, using copy of DNA, plus half
 * of the proteins.
 *
 * Wait for proteinList to double in size;
 * create new daughterCell;
 * get all proteins over geneSize (i.e. number of proteins the cell had
 * when it was first created), and for each:
 *    stop, remove from current cell, add to new cell.
 *
 * Created by john.denny@gmail.com on 13/02/2017.
 */
public class DivideCell extends AminoAcid {

    /**
     * Only allow one instance of DivideCell to run for each cell.
     *
     * @return false if another instance of DivideCell is running,
     * otherwise return true.
     */
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
                    CellListener cellListener = cell.getCellListener();
                    if (cellListener != null) {
                        daughterCell.setCellListener(cellListener);
                        cellListener.onNewCell(daughterCell);
                    }
                    return daughterCell;
                }
                String state = Thread.currentThread().getName() +
                        " cell divide waiting for " +
                        ((geneSize * 2) - proteinSize) +
                        " more proteins";
                MasterDesigner.print(state);
                getProtein().setState(state);
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
