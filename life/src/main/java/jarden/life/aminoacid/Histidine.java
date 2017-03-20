package jarden.life.aminoacid;

import jarden.life.CellResource;
import jarden.life.nucleicacid.Adenine;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.Cytosine;
import jarden.life.nucleicacid.Uracil;

/**
 * Created by john.denny@gmail.com on 18/03/2017.
 */

public class Histidine extends AminoAcid {
    @Override
    public CellResource action(CellResource resource) throws InterruptedException {
        System.out.println("******Histidine*****");
        return null;
    }
    @Override
    public boolean matchCodon(Codon codon) {
        return codon.getFirst() instanceof Cytosine &&
                codon.getSecond() instanceof Adenine &&
                codon.getThird() instanceof Uracil;
    }
    @Override
    public String getName() {
        return "Histidine";
    }
}
