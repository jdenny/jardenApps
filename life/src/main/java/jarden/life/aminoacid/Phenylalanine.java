package jarden.life.aminoacid;

import jarden.life.CellResource;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.Uracil;

/**
 * Data: food.
 *
 * Created by john.denny@gmail.com on 18/03/2017.
 */

public class Phenylalanine extends AminoAcid {
    @Override
    public CellResource action(CellResource resource) throws InterruptedException {
        return null;
    }
    @Override
    public boolean matchCodon(Codon codon) {
        return codon.getFirst() instanceof Uracil &&
                codon.getSecond() instanceof Uracil &&
                codon.getThird() instanceof Uracil;
    }
    @Override
    public int getIndex() {
        return 13;
    }
    @Override
    public String getName() {
        return "Phenylalanine";
    }
    @Override
    public String getShortName() {
        return "Phe";
    }
}
