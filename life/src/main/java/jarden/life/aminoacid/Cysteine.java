package jarden.life.aminoacid;

import jarden.life.CellResource;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.Guanine;
import jarden.life.nucleicacid.Uracil;

/**
 * Turn on code mode.
 * Created by john.denny@gmail.com on 18/03/2017.
 */
public class Cysteine extends AminoAcid {
    @Override
    public CellResource action(CellResource resource) throws InterruptedException {
        return null;
    }
    @Override
    public boolean matchCodon(Codon codon) {
        return codon.getFirst() instanceof Uracil &&
                codon.getSecond() instanceof Guanine &&
                codon.getThird() instanceof Uracil;
    }
    @Override
    public boolean isCode() {
        return true;
    }
    @Override
    public int getIndex() {
        return 4;
    }
    @Override
    public String getName() {
        return "Cysteine";
    }
    @Override
    public String getShortName() {
        return "Cys";
    }
}
