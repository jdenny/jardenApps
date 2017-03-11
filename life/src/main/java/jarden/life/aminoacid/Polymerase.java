package jarden.life.aminoacid;

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
            index = getNextStartIndex(dna, index);
            if (index < 0) {
                index = getNextStartIndex(dna, 0);
                if (index < 0) {
                    throw new IllegalStateException("DNA contains no start-gene");
                }
            }
            index += Nucleotide.startLength; // i.e. first nucleotide after promoter
            nextGeneIndex = getNextGeneIndex(dna, index);
            if (nextGeneIndex < 0) {
                throw new IllegalStateException("DNA gene has no stop-gene");
            }
            cell.setDnaIndex(nextGeneIndex);
        } finally {
            dnaIndexLock.unlock();
        }
        RNA rna = new RNA();
        for (int i = index; i < nextGeneIndex; i += 3) {
            Nucleotide first = cell.waitForNucleotide(dna.getFromTemplate(i), false);
            // TODO: do we need this? I've lost track!
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            Nucleotide second = cell.waitForNucleotide(dna.getFromTemplate(i+1), false);
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            Nucleotide third = cell.waitForNucleotide(dna.getFromTemplate(i+2), false);
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            Codon codon = new Codon(first, second, third);
            rna.add(codon);
        }
        cell.addRNA(rna);
        return null;
	}
    private static int getNextStartIndex(DNA dna, int index) {
        for (int i = index; (i + Nucleotide.startLength) <= dna.size(); i+=3) {
            if (isGeneStart(dna, index)) {
                return index;
            }
        }
        return -1;
    }
    private static boolean isGeneStart(DNA dna, int startIndex) {
        Codon codon = new Codon(dna.getFromMaster(startIndex),
                dna.getFromMaster(startIndex + 1),
                dna.getFromMaster(startIndex + 2));
        return codon.isStart();
    }
    private static int getNextGeneIndex(DNA dna, int index) {
        for (int i = index; (i + 3) <= dna.size(); i+=3) {
            if (isGeneStop(dna, i)) {
                return i + 3; // i.e. position after stopCodon
            }
        }
        return -1;
    }
    private static boolean isGeneStop(DNA dna, int stopIndex) {
        Codon codon = new Codon(dna.getFromMaster(stopIndex),
                dna.getFromMaster(stopIndex + 1),
                dna.getFromMaster(stopIndex + 2));
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
