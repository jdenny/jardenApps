package jarden.life;

import java.util.LinkedList;
import java.util.List;

import jarden.life.aminoacid.AminoAcid;
import jarden.life.aminoacid.CopyDNA;
import jarden.life.aminoacid.DigestFood;
import jarden.life.aminoacid.DivideCell;
import jarden.life.aminoacid.EatFood;
import jarden.life.aminoacid.Polymerase;
import jarden.life.aminoacid.Ribosome;
import jarden.life.aminoacid.WaitForEnoughProteins;
import jarden.life.nucleicacid.Adenine;
import jarden.life.nucleicacid.Cytosine;
import jarden.life.nucleicacid.Guanine;
import jarden.life.nucleicacid.Nucleotide;
import jarden.life.nucleicacid.Thymine;
import jarden.life.nucleicacid.Uracil;

/**
 * Created by john.denny@gmail.com on 24/02/2017.
 */

public class CellFood implements Food {
    private List<AminoAcid> aminoAcidList = new LinkedList<>();
    private List<Nucleotide> nucleotideList = new LinkedList<>();

    @Override
    public List<AminoAcid> getAminoAcidList() {
        return aminoAcidList;
    }
    @Override
    public List<Nucleotide> getNucleotideList() {
        return nucleotideList;
    }

    @Override
    public String getName() {
        return "Cell Food";
    }

    public void addAminoAcids() {
        aminoAcidList.add(new CopyDNA());
        aminoAcidList.add(new DigestFood());
        aminoAcidList.add(new DivideCell());
        aminoAcidList.add(new EatFood());
        aminoAcidList.add(new Ribosome());
        aminoAcidList.add(new Polymerase());
        aminoAcidList.add(new WaitForEnoughProteins());
    }
    public void addNucleotides() {
        for (int i = 0; i < Cell.nucleotideFeedCounts[0]; i++) {
            nucleotideList.add(new Adenine());
        }
        for (int i = 0; i < Cell.nucleotideFeedCounts[1]; i++) {
            nucleotideList.add(new Cytosine());
        }
        for (int i = 0; i < Cell.nucleotideFeedCounts[2]; i++) {
            nucleotideList.add(new Guanine());
        }
        for (int i = 0; i < Cell.nucleotideFeedCounts[3]; i++) {
            nucleotideList.add(new Thymine());
        }
        for (int i = 0; i < Cell.nucleotideFeedCounts[4]; i++) {
            nucleotideList.add(new Uracil());
        }
    }
    /**
     * Food factory - until we work out how to make real food.
     * @param name of amino acid
     * @return object of specified amino acid
     */
    public static Nucleotide makeNucleotide(String name) {
        Nucleotide nucleotide;
        if (name.equals("Adenine")) nucleotide = new Adenine();
        else if (name.equals("Cytosine")) nucleotide = new Cytosine();
        else if (name.equals("Guanine")) nucleotide = new Guanine();
        else if (name.equals("Thymine")) nucleotide = new Thymine();
        else if (name.equals("Uracil")) nucleotide = new Uracil();
        else throw new IllegalArgumentException("unknown nucleotide name: " + name);
        return nucleotide;
    }
}
