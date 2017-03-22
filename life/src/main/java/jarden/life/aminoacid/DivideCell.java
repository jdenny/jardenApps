package jarden.life.aminoacid;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;

import jarden.life.Cell;
import jarden.life.CellResource;
import jarden.life.Protein;
import jarden.life.Regulator;
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
 *    stop, remove from current cell, addAminoAcid to new cell.
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
        Lock regulatorListLock = cell.getRegulatorListLock();
        Lock daughterRegulatorListLock = daughterCell.getRegulatorListLock();
        List<Regulator> daughterRegulatorList = daughterCell.getRegulatorList();
        int geneSize = cell.getGeneSize();
        int proteinListSize = proteinList.size();
        if (proteinListSize < geneSize * 2) {
            System.out.println("DivideCell.proteinListSize=" + proteinListSize);
        }
        // dis should stoppem floppen:
        daughterRegulatorListLock.lockInterruptibly();
        regulatorListLock.lockInterruptibly();
        try {
            // find duplicate proteins and move them
            // to daughter cell:
            int[] dnaIndices = new int[geneSize];
            int indicesAdded = 0;
            int dnaIndex;
            Protein protein;
            Regulator regulator;
            Regulator daughterRegulator;
            Iterator<Protein> proteinListIterator = proteinList.iterator();
            while (proteinListIterator.hasNext()) {
                protein = proteinListIterator.next();
                dnaIndex = protein.getRegulator().getDnaIndex();
                if (isInArray(dnaIndex, dnaIndices, indicesAdded)) {
                    // protein of this type already found,
                    // so move this one to the new cell
                    proteinListIterator.remove();
                    protein.stop();
                    regulator = protein.getRegulator();
                    regulator.decrementCounts();
                    daughterRegulator =
                            daughterRegulatorList.get(regulator.getRegulatorListIndex());
                    protein.setCell(daughterCell, daughterRegulator);
                    daughterCell.addProtein(protein);
                } else {
                    dnaIndices[indicesAdded++] = dnaIndex;
                }
            }
            cell.getCellEnvironment().addCell(daughterCell);
            cell.getRnaBelowTargetCondition().signalAll();
            return daughterCell;
        } finally {
            regulatorListLock.unlock();
            daughterRegulatorListLock.unlock();
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
    @Override
    public boolean matchCodon(Codon codon) {
        return codon.getFirst() instanceof Uracil &&
                codon.getSecond() instanceof Adenine &&
                codon.getThird() instanceof Cytosine;
    }
    @Override
    public int getIndex() {
        return 22;
    }
    @Override
    public String getName() {
        return "DivideCell";
    }
    @Override
    public String getShortName() {
        return "Divide";
    }
}
