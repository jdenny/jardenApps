package jarden.life.aminoacid;

import jarden.life.CellResource;
import jarden.life.nucleicacid.Adenine;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.Uracil;

/**
 * Created by john.denny@gmail.com on 18/03/2017.
 */

public class Tyrosine extends AminoAcid {
    @Override
    public CellResource action(CellResource resource) throws InterruptedException {
        return null;
    }
    @Override
    public boolean matchCodon(Codon codon) {
        return codon.getFirst() instanceof Uracil &&
                codon.getSecond() instanceof Adenine &&
                codon.getThird() instanceof Uracil;
    }
    @Override
    public int getIndex() {
        return 18;
    }
    @Override
    public String getName() {
        return "Tyrosine";
    }
    @Override
    public String getShortName() {
        return "Tyr";
    }
}
