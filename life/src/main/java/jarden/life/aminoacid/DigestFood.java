package jarden.life.aminoacid;

import java.util.List;

import jarden.life.Cell;
import jarden.life.Food;
import jarden.life.MasterDesigner;
import jarden.life.Protein;
import jarden.life.nucleicacid.Adenine;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.Cytosine;
import jarden.life.nucleicacid.DNA;
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
    public DigestFood(Cell cell) {
        super(cell);
    }
    public Object action(Object object) {
        Food food = getCell().waitForFood(null);
        if (Thread.interrupted()) {
            Thread.currentThread().interrupt();
            return null;
        }
        Cell cell = getCell();
        List<Nucleotide> nucleotides = food.getNucleotideList();
        if (nucleotides != null && nucleotides.size() > 0) {
            cell.addNucleotides(nucleotides);
        }
        DNA dna = food.getDNA();
        if (dna != null && dna.size() > 0) {
            cell.addNucleotides(dna);
        }
        List<AminoAcid> aminoAcids = food.getAminoAcidList();
        if (aminoAcids != null && aminoAcids.size() > 0) {
            cell.addAminoAcids(aminoAcids);
        }
        List<Protein> proteins = food.getProteinList();
        if (proteins != null) {
            for (Protein protein: proteins) {
                cell.addAminoAcids(protein.getAminoAcidList());
            }
        }
        return null;
    }
    public String getName() {
        return "DigestFood";
    }
    public boolean matchCodon(Codon codon) {
        return codon.getFirst() instanceof Uracil &&
                codon.getSecond() instanceof Cytosine &&
                codon.getThird() instanceof Adenine;
    }
}
