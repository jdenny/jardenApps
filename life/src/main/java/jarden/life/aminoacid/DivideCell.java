package jarden.life.aminoacid;

import java.util.List;
import java.util.concurrent.locks.Condition;
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
        Cell daughterCell = new Cell(daughterDNA, cell.getCellEnvironment());
        daughterCell.setGeneration(cell.getGeneration() + 1);
        List<Protein> proteinList = cell.getProteinList();
        Lock proteinListLock = cell.getProteinListLock();
        int geneSize = cell.getGeneSize();
        proteinListLock.lockInterruptibly();
        try {
            /* TODO: answer this question
            is it possible that either cell could end up with the
            wrong number of proteins? E.g. before split:
            parentCell: p1, p2, p3, p1, p1, p2, p3
            after split:
            parent: p1, p2, p3 (geneSize=3)
            child: p1, p1, p2, p3 (geneSize=4)
            then when child is ready to split:
            p1, p1, p2, p3, p2, p3
            after split:
            parent: p1, p1, p2, p3
            child: p2, p3
             */
            int proteinListSize = proteinList.size();
            for (int i = geneSize; i < proteinListSize; i++) {
                Protein protein = proteinList.remove(geneSize);
                protein.stop();
                protein.getRegulator().decrementCounts();
                protein.setCell(daughterCell);
                daughterCell.addProtein(protein);
            }
            cell.getCellEnvironment().addCell(daughterCell);
        } finally {
            proteinListLock.unlock();
        }
        Lock regulatorListLock = cell.getRegulatorListLock();
        regulatorListLock.lockInterruptibly();
        try {
            cell.getRnaBelowTargetCondition().signalAll();
            return daughterCell;
        } finally {
            regulatorListLock.unlock();
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
