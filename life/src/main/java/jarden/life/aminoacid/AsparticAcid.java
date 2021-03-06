package jarden.life.aminoacid;

import jarden.life.CellResource;
import jarden.life.nucleicacid.Adenine;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.Guanine;
import jarden.life.nucleicacid.Uracil;

/**
 * Turn on data mode.
 * Created by john.denny@gmail.com on 18/03/2017.
 */
public class AsparticAcid extends AminoAcid {
    @Override
    public CellResource action(CellResource resource) throws InterruptedException {
        return null;
    }
    @Override
    public boolean matchCodon(Codon codon) {
        return codon.getFirst() instanceof Guanine &&
                codon.getSecond() instanceof Adenine &&
                codon.getThird() instanceof Uracil;
    }
    @Override
    public boolean isData() {
        return true;
    }
    @Override
    public int getIndex() {
        return 3;
    }
    @Override
    public String getName() {
        return "AsparticAcid";
    }
    @Override
    public String getShortName() {
        return "Asp";
    }
}
