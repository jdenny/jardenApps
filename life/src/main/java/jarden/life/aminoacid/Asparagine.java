package jarden.life.aminoacid;

import jarden.life.Cell;
import jarden.life.CellResource;
import jarden.life.Protein;
import jarden.life.TargetResource;
import jarden.life.nucleicacid.Adenine;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.RNA;
import jarden.life.nucleicacid.Uracil;

/**
 * Data: needMoreFood
 * Created by john.denny@gmail.com on 18/03/2017.
 */

public class Asparagine extends AminoAcid {
    @Override
    public CellResource action(CellResource notUsed) throws InterruptedException {
        return null;
    }
    @Override
    public boolean matchCodon(Codon codon) {
        return codon.getFirst() instanceof Adenine &&
                codon.getSecond() instanceof Adenine &&
                codon.getThird() instanceof Uracil;
    }
    @Override
    public int getIndex() {
        return 2;
    }
    @Override
    public String getName() {
        return "Asparagine";
    }
    @Override
    public String getShortName() {
        return "Asn";
    }
}
