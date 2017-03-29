package jarden.life.aminoacid;

import jarden.life.CellResource;
import jarden.life.Protein;
import jarden.life.Regulator;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.Guanine;
import jarden.life.nucleicacid.Uracil;

/**
 * Run only one of these proteins.
 * The only current use of this so far is in DivideCell, where
 * we don't want 2 threads trying to divide the cell.
 * Created by john.denny@gmail.com on 18/03/2017.
 */

public class Glycine extends AminoAcid {
    @Override
    public CellResource action(CellResource resource) throws InterruptedException {
        Protein protein = getProtein();
        Regulator regulator = protein.getRegulator();
        regulator.setTargetRunCt(1);
        return null;
    }
    @Override
    public boolean matchCodon(Codon codon) {
        return codon.getFirst() instanceof Guanine &&
                codon.getSecond() instanceof Guanine &&
                codon.getThird() instanceof Uracil;
    }
    @Override
    public int getIndex() {
        return 7;
    }
    @Override
    public String getName() {
        return "Glycine";
    }
    @Override
    public String getShortName() {
        return "Gly";
    }
}
