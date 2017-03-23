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
 * Create new target resource, and addAminoAcid it to the Protein
 * Created by john.denny@gmail.com on 18/03/2017.
 */

public class Asparagine extends AminoAcid {
    @Override
    public CellResource action(CellResource notUsed) throws InterruptedException {
        /*!!
        Cell cell = getCell();
        Protein protein = getProtein();
        AminoAcid aminoAcidAsData = protein.getAminoAcid(-2);
        TargetResource targetResource;
        if (aminoAcidAsData instanceof Asparagine) {
            targetResource = new Protein(cell);
        } else if (aminoAcidAsData instanceof Arginine) {
            targetResource = new RNA();
        } else {
            throw new IllegalStateException(
                "unrecognised aminoAcidAsData: " + aminoAcidAsData);
        }
        protein.setTargetResource(targetResource);
        return targetResource;
        */
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
