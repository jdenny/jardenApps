package jarden.life.aminoacid;

import java.util.ListIterator;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import jarden.life.Cell;
import jarden.life.CellResource;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.DNA;
import jarden.life.nucleicacid.Guanine;
import jarden.life.nucleicacid.Nucleotide;
import jarden.life.nucleicacid.RNA;
import jarden.life.nucleicacid.Uracil;

/*
 * This protein is called RNA Polymerase.
 * Sequence of real DNA:
 * 		regulator gene: builds regulator protein for this operon
 * 		promoter: where polymerase docks; simplified here as TATAAT
 * 		operator: where regulator protein docks
 * 		operon: group of related genes
 * 		terminator: end of operon; simplified here as UAA 
 */
public class Polymerase extends AminoAcid {

    @Override
	public CellResource action(CellResource notUsed) throws InterruptedException {
        /*
        Keep dnaIndex in cell, along with dna;
        action()
            put lock on dnaIndex;
            get dna & dnaIndex from cell;
            index = next promoter >= index
            if no promoter:
                index = 0
                index = next promoter >= index
                if no promoter: throw exception
            find next stop
            set dnaIndex to position after stop
            release lock on dnaIndex
            start building RNA!
         */
        Cell cell = getCell();
        Lock rnaListLock = cell.getRnaListLock();
        Condition needMoreRNA = cell.getNeedMoreRNA();
        rnaListLock.lockInterruptibly();
        try {
            while (cell.getRNAList().size() >= cell.getGeneSize()) {
                cell.logId("waiting for needMoreRNA");
                needMoreRNA.await();
            }
        } finally {
            rnaListLock.unlock();
        }
        DNA dna = cell.getDNA(); // get it each time, in case it's become corrupted!
        Lock dnaIndexLock = cell.getDnaIndexLock();
        int index, nextGeneIndex;
        dnaIndexLock.lockInterruptibly();
        try {
            index = cell.getDnaIndex(); // get it each time, in case changed by another protein
            index = getNextPromoterIndex(dna, index);
            if (index < 0) {
                index = getNextPromoterIndex(dna, 0);
                if (index < 0) {
                    throw new IllegalStateException("DNA contains no promoter");
                }
            }
            index += Nucleotide.promoterLength; // i.e. first nucleotide after promoter
            nextGeneIndex = getNextGeneIndex(dna, index);
            if (nextGeneIndex < 0) {
                throw new IllegalStateException("DNA gene has no terminator");
            }
            cell.setDnaIndex(nextGeneIndex);
        } finally {
            dnaIndexLock.unlock();
        }
        RNA rna = new RNA();
        for (int i = index; i < nextGeneIndex; i += 3) {
            Nucleotide first = cell.waitForNucleotide(dna.get(i), false);
            // TODO: do we need this? I've lost track!
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            Nucleotide second = cell.waitForNucleotide(dna.get(i+1), false);
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            Nucleotide third = cell.waitForNucleotide(dna.get(i+2), false);
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            Codon codon = new Codon(first, second, third);
            rna.add(codon);
        }
        cell.addRNA(rna);
        return null;
	}
    private static int getNextPromoterIndex(DNA dna, int index) {
        for (int i = index; (i + Nucleotide.promoterLength) <= dna.size(); i+=3) {
            if (isPromoter(dna, index)) {
                return index;
            }
        }
        return -1;
    }
    private static boolean isPromoter(DNA dna, int index) {
        for (int j = 0; j < Nucleotide.promoterLength; j++) {
            Nucleotide nucleotide = dna.get(index + j);
            if (!(nucleotide.getCode() == Nucleotide.promoterCode.charAt(j))) {
                return false; // i.e. NOT a promoter
            }
        }
        return true;
    }
    private static int getNextGeneIndex(DNA dna, int index) {
        for (int i = index; (i + 3) <= dna.size(); i+=3) {
            if (isStop(dna, i)) {
                return i + 3; // i.e. position after stopCodon
            }
        }
        return -1;
    }
    private static boolean isStop(DNA dna, int stopIndex) {
        Codon codon = new Codon(dna.get(stopIndex), dna.get(stopIndex + 1),
                dna.get(stopIndex + 2));
        return codon.isStop();
    }
    @Override
	public String getName() {
		return "Polymerase";
	}
    @Override
	public boolean matchCodon(Codon codon) {
        return codon.getFirst() instanceof Uracil &&
                codon.getSecond() instanceof Guanine &&
                codon.getThird() instanceof Guanine;
    }
}
