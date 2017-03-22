package jarden.life.aminoacid;

import java.util.List;

import jarden.life.Cell;
import jarden.life.CellResource;
import jarden.life.Food;
import jarden.life.nucleicacid.Adenine;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.Cytosine;
import jarden.life.nucleicacid.Nucleotide;
import jarden.life.nucleicacid.Uracil;

/**
 * Get raw materials for cell from food.
 * DNA -> nucleotides
 * proteins -> aminoAcids
 * nucleotides -> nucleotides
 * aminoAcids -> aminoAcids
 *
 * Created by john.denny@gmail.com on 13/02/2017.
 */

public class DigestFood extends AminoAcid {
    public CellResource action(CellResource _food) throws InterruptedException {
        Food food = (Food) _food;
        Cell cell = getCell();
        List<Nucleotide> nucleotides = food.getNucleotideList();
        if (nucleotides != null && nucleotides.size() > 0) {
            cell.addNucleotides(nucleotides);
        }
        List<AminoAcid> aminoAcids = food.getAminoAcidList();
        if (aminoAcids != null && aminoAcids.size() > 0) {
            cell.addAminoAcids(aminoAcids);
        }
        return null;
    }
    @Override
    public boolean matchCodon(Codon codon) {
        return codon.getFirst() instanceof Uracil &&
                codon.getSecond() instanceof Cytosine &&
                codon.getThird() instanceof Adenine;
    }
    @Override
    public int getIndex() {
        return 21;
    }
    @Override
    public String getName() {
        return "DigestFood";
    }
    @Override
    public String getShortName() {
        return "Digest";
    }
}
