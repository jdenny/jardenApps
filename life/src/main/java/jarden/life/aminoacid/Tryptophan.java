package jarden.life.aminoacid;

import jarden.life.Cell;
import jarden.life.CellResource;
import jarden.life.Protein;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.Guanine;
import jarden.life.nucleicacid.Nucleotide;
import jarden.life.nucleicacid.Uracil;

/**
 * Generic - wait for resource.
 * Created by john.denny@gmail.com on 18/03/2017.
 */
public class Tryptophan extends AminoAcid {
    private AminoAcid resourceType = null;

    @Override
    public CellResource action(CellResource resource)
            throws InterruptedException {
        Protein protein = getProtein();
        Cell cell = getCell();
        if (resource != null) {
            if (resource instanceof Codon) {
                return cell.waitForAminoAcid((Codon) resource);
            } else if (resource instanceof Nucleotide) {
                return cell.waitForNucleotide((Nucleotide) resource,
                        protein.isForDna());
            }
        }
        if (resourceType == null) {
            resourceType = protein.getData();
        }
        if (resourceType instanceof Arginine) {
            return cell.waitForRNA();
        } else if (resourceType instanceof Isoleucine) {
            return cell.getDNA();
        } else if (resourceType instanceof Glutamine) {
            cell.waitForCellReadyToDivide();
            return null;
        } else if (resourceType instanceof Phenylalanine) {
            return cell.waitForFoodFromCell();
        } else if (resourceType instanceof Histidine) {
            return cell.waitForFoodFromEnvironment();
        } else if (resourceType instanceof Proline) {
            return cell.waitForRnaBelowTarget();
        } else if (resourceType instanceof Asparagine) {
            cell.waitForCellNeedsFood();
            return null;
        } else {
            cell.throwError("unrecognised resource type: " + resourceType);
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
