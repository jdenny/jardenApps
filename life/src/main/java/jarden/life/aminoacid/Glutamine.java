package jarden.life.aminoacid;

import jarden.life.CellResource;
import jarden.life.nucleicacid.Adenine;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.Cytosine;

/**
 * Data: readyToDivide.
 * Created by john.denny@gmail.com on 18/03/2017.
 */

public class Glutamine extends AminoAcid {
    @Override
    public CellResource action(CellResource resource) throws InterruptedException {
        return null;
    }
    @Override
    public boolean matchCodon(Codon codon) {
        return codon.getFirst() instanceof Cytosine &&
                codon.getSecond() instanceof Adenine &&
                codon.getThird() instanceof Adenine;
    }
    @Override
    public int getIndex() {
        return 6;
    }
    @Override
    public String getName() {
        return "Glutamine";
    }
    @Override
    public String getShortName() {
        return "Gln";
    }
}
