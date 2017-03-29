package jarden.life.aminoacid;

import jarden.life.Cell;
import jarden.life.CellResource;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.Cytosine;
import jarden.life.nucleicacid.Guanine;
import jarden.life.nucleicacid.Nucleotide;
import jarden.life.nucleicacid.Uracil;

/**
 * Convert DNA codon to RNA codon. May be too specific!
 * TODO: work out if this can be generalised
 * Created by john.denny@gmail.com on 18/03/2017.
 */

public class Alanine extends AminoAcid {
    @Override
    public CellResource action(CellResource _codon) throws InterruptedException {
        Codon codon = (Codon) _codon;
        Cell cell = getCell();
        Nucleotide first = cell.waitForNucleotide(codon.getFirst(), false);
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        Nucleotide second = cell.waitForNucleotide(codon.getSecond(), false);
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        Nucleotide third = cell.waitForNucleotide(codon.getThird(), false);
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        return new Codon(first, second, third);
    }
    @Override
    public boolean matchCodon(Codon codon) {
        return codon.getFirst() instanceof Guanine &&
                codon.getSecond() instanceof Cytosine &&
                codon.getThird() instanceof Uracil;
    }
    @Override
    public int getIndex() {
        return 0;
    }
    @Override
    public String getName() {
        return "Alanine";
    }
    @Override
    public String getShortName() {
        return "Ala";
    }
}
