package jarden.life.obsolete;

import jarden.life.Cell;
import jarden.life.CellResource;
import jarden.life.Protein;
import jarden.life.aminoacid.AminoAcid;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.Cytosine;
import jarden.life.nucleicacid.RNA;
import jarden.life.nucleicacid.Uracil;

/**
 * Created by john.denny@gmail.com on 14/03/2017.
 */

public class Ribosome extends AminoAcid {

    @Override
    public CellResource action(CellResource _rna) throws InterruptedException {
        RNA rna = (RNA) _rna;
        Cell cell = getCell();
        Protein newProtein = (Protein) rna.getTargetResource();
        Codon codon;
        AminoAcid aminoAcid;
        while (rna.hasNext()) {
            codon = rna.next();
            aminoAcid = cell.waitForAminoAcid(codon);
            newProtein.addAminoAcid(aminoAcid);
        }
        cell.addProtein(newProtein);
        return null;
    }
    @Override
    public boolean matchCodon(Codon codon) {
        return codon.getFirst() instanceof Uracil &&
                codon.getSecond() instanceof Cytosine &&
                codon.getThird() instanceof Cytosine;
    }
    @Override
    public int getIndex() {
        return -1;
    }
    @Override
    public String getName() {
        return "Ribosome";
    }
    @Override
    public String getShortName() {
        return "Ribosome";
    }
}
