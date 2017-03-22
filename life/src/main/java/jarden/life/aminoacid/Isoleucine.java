package jarden.life.aminoacid;

import jarden.life.CellResource;
import jarden.life.nucleicacid.Adenine;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.Uracil;

/**
 * Created by john.denny@gmail.com on 18/03/2017.
 */

public class Isoleucine extends AminoAcid {
    @Override
    public CellResource action(CellResource resource) throws InterruptedException {
        return null;
    }
    @Override
    public boolean matchCodon(Codon codon) {
        return codon.getFirst() instanceof Adenine &&
                codon.getSecond() instanceof Uracil &&
                codon.getThird() instanceof Uracil;
    }
    @Override
    public int getIndex() {
        return 9;
    }
    @Override
    public String getName() {
        return "Isoleucine";
    }
    @Override
    public String getShortName() {
        return "Ile";
    }
}
