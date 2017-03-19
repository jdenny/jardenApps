package jarden.life.aminoacid;

import jarden.life.Cell;
import jarden.life.CellResource;
import jarden.life.Protein;
import jarden.life.nucleicacid.Adenine;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.RNA;
import jarden.life.nucleicacid.Uracil;

/**
 * Created by john.denny@gmail.com on 14/03/2017.
 */

public class Ribosome extends AminoAcid {

    @Override
    public CellResource action(int aminoAcidIndex, CellResource _rna) throws InterruptedException {
        RNA rna = (RNA) _rna;
        Cell cell = getCell();
        //!! RNA rna = cell.waitForRNA();
        Protein newProtein = rna.getNewProtein();
        int index = 0;
        Codon codon;
        AminoAcid aminoAcid;
        while (true) {
            codon = rna.getCodon(index++);
            if (codon.isStop()) {
                break;
            }
            aminoAcid = cell.waitForAminoAcid(codon);
            newProtein.add(aminoAcid);
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
                codon.getSecond() instanceof Adenine &&
                codon.getThird() instanceof Uracil;
    }
}
