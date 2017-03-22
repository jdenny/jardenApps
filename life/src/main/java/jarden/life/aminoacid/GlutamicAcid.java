package jarden.life.aminoacid;

import jarden.life.Cell;
import jarden.life.CellResource;
import jarden.life.Protein;
import jarden.life.nucleicacid.Adenine;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.Guanine;
import jarden.life.nucleicacid.RNA;

/**
 * Add resource to cell.
 * Created by john.denny@gmail.com on 18/03/2017.
 */

public class GlutamicAcid extends AminoAcid {
    @Override
    public CellResource action(CellResource resource) throws InterruptedException {
        Cell cell = getCell();
        if (resource instanceof Protein) {
            cell.addProtein((Protein) resource);
        } else if (resource instanceof RNA) {
            cell.addRNA((RNA) resource);
        } else {
            throw new IllegalArgumentException(
                "unrecognised resource: " + resource);
        }
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
