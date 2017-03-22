package jarden.life.aminoacid;

import jarden.life.Cell;
import jarden.life.CellResource;
import jarden.life.Protein;
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
        Protein newProtein = rna.getNewProtein();
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
    public String getName() {
        return "Ribosome";
    }
    @Override
    public boolean matchCodon(Codon codon) {
        return codon.getFirst() instanceof Uracil &&
                codon.getSecond() instanceof Cytosine &&
                codon.getThird() instanceof Cytosine;
    }
}
