package jarden.life.aminoacid;

import jarden.life.CellResource;
import jarden.life.nucleicacid.Adenine;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.Guanine;

/**
 * Created by john.denny@gmail.com on 18/03/2017.
 */

public class GlutamicAcid extends AminoAcid {
    @Override
    public CellResource action(CellResource resource) throws InterruptedException {
        return null;
    }
    @Override
    public boolean matchCodon(Codon codon) {
        return codon.getFirst() instanceof Guanine &&
                codon.getSecond() instanceof Adenine &&
                codon.getThird() instanceof Adenine;
    }
    @Override
    public String getName() {
        return "GlutamicAcid";
    }
}