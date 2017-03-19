package jarden.life.aminoacid;

import jarden.life.Cell;
import jarden.life.CellResource;
import jarden.life.Protein;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.Guanine;
import jarden.life.nucleicacid.Uracil;

/**
 * Created by john.denny@gmail.com on 18/03/2017.
 */

public class Tryptophan extends AminoAcid {
    @Override
    public CellResource action(CellResource resource)
            throws InterruptedException {
        Protein protein = getProtein();
        Cell cell = getCell();
        AminoAcid aminoAcid = protein.getAminoAcid(-2);
        if (aminoAcid instanceof Arginine) {
            // resourceType is RNA
            return cell.waitForRNA();
        } else if (aminoAcid instanceof Phenylalanine) {
            return cell.waitForFood();
        } else if (aminoAcid instanceof Proline) {
            return cell.waitForRnaBelowTarget();
        } else {
            throw new IllegalStateException("unrecognised resource type: " + aminoAcid);
        }
    }
    @Override
    public boolean matchCodon(Codon codon) {
        return codon.getFirst() instanceof Uracil &&
                codon.getSecond() instanceof Guanine &&
                codon.getThird() instanceof Guanine;
    }
    @Override
    public String getName() {
        return "Tryptophan";
    }
}
