package jarden.life.aminoacid;

import jarden.life.Cell;
import jarden.life.CellResource;
import jarden.life.Protein;
import jarden.life.nucleicacid.Adenine;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.Guanine;
import jarden.life.nucleicacid.Nucleotide;
import jarden.life.nucleicacid.Uracil;

/**
 * Generic - wait for resource.
 * Created by john.denny@gmail.com on 18/03/2017.
 */
public class Tryptophan extends AminoAcid {
    @Override
    public CellResource action(CellResource resource)
            throws InterruptedException {
        Protein protein = getProtein();
        Cell cell = getCell();
        if (resource != null) {
            if (resource instanceof Codon) {
                return cell.waitForAminoAcid((Codon) resource);
            } else if (resource instanceof Nucleotide) {
                return cell.waitForNucleotide((Nucleotide) resource, false);
            } else {
                cell.throwError("unrecognised resource: " + resource);
                return null;
            }
        }
        AminoAcid aminoAcid = protein.getAminoAcid(-2);
        if (aminoAcid instanceof Arginine) {
            return cell.waitForRNA();
        } else if (aminoAcid instanceof Glutamine) {
            cell.waitForCellReadyToDivide();
            return null;
        } else if (aminoAcid instanceof Phenylalanine) {
            return cell.waitForFood();
        } else if (aminoAcid instanceof Proline) {
            return cell.waitForRnaBelowTarget();
        } else {
            cell.throwError("unrecognised resource type: " + aminoAcid);
            return null;
        }
    }
    @Override
    public boolean matchCodon(Codon codon) {
        return codon.getFirst() instanceof Uracil &&
                codon.getSecond() instanceof Guanine &&
                codon.getThird() instanceof Guanine;
    }
    @Override
    public int getIndex() {
        return 17;
    }
    @Override
    public String getName() {
        return "Tryptophan";
    }
    @Override
    public String getShortName() {
        return "Trp";
    }
}
