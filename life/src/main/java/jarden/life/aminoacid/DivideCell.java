package jarden.life.aminoacid;

import java.util.List;
import java.util.concurrent.locks.Lock;

import jarden.life.Cell;
import jarden.life.CellResource;
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

    public CellResource action(CellResource _dna) throws InterruptedException {
        DNA daughterDNA = (DNA) _dna;
        Cell cell = getCell();
        List<Protein> proteinList = cell.getProteinList();
        Lock proteinListLock = cell.getProteinListLock();
        int geneSize = cell.getGeneSize();
        proteinListLock.lockInterruptibly();
        try {
            Cell daughterCell = new Cell(daughterDNA, cell);
            daughterCell.setGeneration(cell.getGeneration() + 1);
            int newProteinCount = proteinList.size();
            for (int i = geneSize; i < newProteinCount; i++) {
                Protein protein = proteinList.remove(geneSize);
                Thread proteinThread = protein.getThread();
                if (proteinThread != null && proteinThread.isAlive()) {
                    protein.stop();
                    cell.logId("divideCell requested stop to protein " + protein);
                    proteinThread.join(300);
                    if (proteinThread.isAlive()) {
                        cell.logId(proteinThread + " didn't die; state=" +
                                proteinThread.getState() +
                                "; moving it anyway**************");
                    } else {
                        cell.logId("divideCell protein stopped: " + protein);
                    }
                } else {
                    cell.logId("divideCell detected no thread for protein " + protein);
                }
                protein.setCell(daughterCell);
                daughterCell.addProtein(protein);
            }
            if (!cell.cellReadyToDivide()) {
                cell.getNeedMoreProteins().signalAll();
            }
            return daughterCell;
        } finally {
            proteinListLock.unlock();
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
