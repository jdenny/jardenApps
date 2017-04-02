package jarden.life.aminoacid;

import jarden.life.Cell;
import jarden.life.CellResource;
import jarden.life.Food;
import jarden.life.Protein;
import jarden.life.nucleicacid.Adenine;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.Guanine;
import jarden.life.nucleicacid.Nucleotide;
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
        } else if (resource instanceof Food) {
            cell.addFood((Food) resource);
        } else {
            cell.throwError("unrecognised resource: " + resource);
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
    public int getIndex() {
        return 5;
    }
    @Override
    public String getName() {
        return "GlutamicAcid";
    }
    @Override
    public String getShortName() {
        return "Glu";
    }
}
