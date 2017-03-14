package jarden.life.aminoacid;

import java.util.Iterator;
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

            To fix:
                int[] dnaIndices = new int[geneSize];
                for each protein in proteinList
                  get dnaIndex
                  if already in list, move to new cell
                  simple!
             */
            // find duplicate proteins and move them
            // to daughter cell:
            int[] dnaIndices = new int[geneSize];
            int indicesAdded = 0;
            int dnaIndex;
            Protein protein;
            Iterator<Protein> proteinListIterator = proteinList.iterator();
            while (proteinListIterator.hasNext()) {
                protein = proteinListIterator.next();
                dnaIndex = protein.getRegulator().getDnaIndex();
                if (isInArray(dnaIndex, dnaIndices, indicesAdded)) {
                    // protein of this type already found,
                    // so move this one to the new cell
                    proteinListIterator.remove();
                    protein.stop();
                    protein.getRegulator().decrementCounts();
                    protein.setCell(daughterCell);
                    daughterCell.addProtein(protein);
                } else {
                    dnaIndices[indicesAdded++] = dnaIndex;
                }
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

    /**
     * Find if number is in the first numbersAdded elements of array.
     * @param number to try to find in array
     * @param array of numbers
     * @param numbersAdded is how many numbers in the array we look through
     * @return true if number is in the first numbersAdded elements of array
     */
    private static boolean isInArray(int number, int[] array, int numbersAdded) {
        for (int i = 0; i < numbersAdded; i++) {
            if (number == array[i]) return true;
        }
        return false;
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
