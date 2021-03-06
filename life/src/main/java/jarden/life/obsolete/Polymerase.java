package jarden.life.obsolete;

/**
 * Created by john.denny@gmail.com on 29/03/2017.
 */

import jarden.life.Cell;
import jarden.life.CellResource;
import jarden.life.Protein;
import jarden.life.Regulator;
import jarden.life.aminoacid.AminoAcid;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.Cytosine;
import jarden.life.nucleicacid.DNA;
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
    public CellResource action(CellResource _regulator) throws InterruptedException {
        Regulator regulator = (Regulator) _regulator;
        Cell cell = getCell();
        DNA dna = cell.getDNA(); // get it each time, in case it's become corrupted!
        int index = regulator.getGeneStartIndex();
        int nextStopIndex = getNextStopIndex(dna, index);
        if (nextStopIndex < 0) {
            cell.throwError("DNA gene has no stop-gene");
            return null;
        }
        RNA rna = new RNA();
        for (int i = index; i < nextStopIndex; i += 3) {
            Nucleotide first = cell.waitForNucleotide(dna.getFromTemplate(i), false);
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
            rna.addCodon(codon);
        }
        Protein newProtein = new Protein(cell);
        newProtein.setRegulator(regulator);
        rna.setNewProtein(newProtein);
        cell.addRNA(rna);
        return null;
    }
    public static int getNextStartIndex(DNA dna, int index) {
        for (int i = index; (i + Nucleotide.startLength) <= dna.size(); i+=3) {
            if (isGeneStart(dna, i)) {
                return i;
            }
        }
        return -1;
    }
    // TODO: move these 4 methods into Cell; no longer public, static
    // and no need to pass dna as parameter.
    private static boolean isGeneStart(DNA dna, int startIndex) {
        Codon codon = new Codon(dna.getFromMaster(startIndex),
                dna.getFromMaster(startIndex + 1),
                dna.getFromMaster(startIndex + 2));
        return codon.isStart();
    }
    public static int getNextStopIndex(DNA dna, int index) {
        for (int i = index; (i + 3) <= dna.size(); i+=3) {
            if (isGeneStop(dna, i)) {
                return i;
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
    public boolean matchCodon(Codon codon) {
        return codon.getFirst() instanceof Uracil &&
                codon.getSecond() instanceof Uracil &&
                codon.getThird() instanceof Cytosine;
    }
    @Override
    public int getIndex() {
        return 24;
    }
    @Override
    public String getName() {
        return "Polymerase";
    }
    @Override
    public String getShortName() {
        return "Polymerase";
    }
}
