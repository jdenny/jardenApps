package jarden.life.aminoacid;

import jarden.life.CellResource;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.Cytosine;
import jarden.life.nucleicacid.Guanine;
import jarden.life.nucleicacid.Uracil;

/**
 * As data: RNA
 * Created by john.denny@gmail.com on 18/03/2017.
 */

public class Arginine extends AminoAcid {
    @Override
    public CellResource action(CellResource resource) throws InterruptedException {
        return null;
    }
    @Override
    public boolean matchCodon(Codon codon) {
        return codon.getFirst() instanceof Cytosine &&
                codon.getSecond() instanceof Guanine &&
                codon.getThird() instanceof Uracil;
    }
    @Override
    public int getIndex() {
        return 1;
    }
    @Override
    public String getName() {
        return "Arginine";
    }
    @Override
    public String getShortName() {
        return "Arg";
    }
}
