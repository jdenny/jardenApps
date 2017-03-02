package jarden.life;

import java.util.LinkedList;
import java.util.List;

import jarden.life.aminoacid.AddAminoAcidToProtein;
import jarden.life.aminoacid.AminoAcid;
import jarden.life.aminoacid.CopyDNA;
import jarden.life.aminoacid.DigestFood;
import jarden.life.aminoacid.DivideCell;
import jarden.life.aminoacid.EatFood;
import jarden.life.aminoacid.FindNextGene;
import jarden.life.aminoacid.GetAminoAcidFromCodon;
import jarden.life.aminoacid.GetCodonFromRNA;
import jarden.life.aminoacid.GetRNAFromGene;
import jarden.life.aminoacid.WaitForEnoughProteins;
import jarden.life.nucleicacid.Adenine;
import jarden.life.nucleicacid.Cytosine;
import jarden.life.nucleicacid.DNA;
import jarden.life.nucleicacid.Guanine;
import jarden.life.nucleicacid.Nucleotide;
import jarden.life.nucleicacid.RNA;
import jarden.life.nucleicacid.Thymine;
import jarden.life.nucleicacid.Uracil;

/**
 * Created by john.denny@gmail.com on 24/02/2017.
 */

public class CellFood implements Food {
    private List<Protein> proteinList = new LinkedList<>();
    private List<AminoAcid> aminoAcidList = new LinkedList<>();
    private List<RNA> rnaList = new LinkedList<>();
    private List<Nucleotide> nucleotideList = new LinkedList<>();

    /**
     * Food factory - until we work out how to make real food.
     * @param name of amino acid
     * @return object of specified amino acid
     */
    public static AminoAcid makeAminoAcid(String name) {
        AminoAcid aminoAcid;
        if (name.equals("AddAminoAcidToProtein")) aminoAcid = new AddAminoAcidToProtein();
        else if (name.equals("DigestFood")) aminoAcid = new DigestFood();
        else if (name.equals("DivideCell")) aminoAcid = new DivideCell();
        else if (name.equals("FindNextGene")) aminoAcid = new FindNextGene();
        else if (name.equals("GetAminoAcidFromCodon")) aminoAcid = new GetAminoAcidFromCodon();
        else if (name.equals("GetCodonFromRNA")) aminoAcid = new GetCodonFromRNA();
        else if (name.equals("GetRNAFromGene")) aminoAcid = new GetRNAFromGene();
        else throw new IllegalArgumentException("unknown Amino Acid name: " + name);
        return aminoAcid;
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
    @Override
    public DNA getDNA() {
        return null;
    }
    @Override
    public List<Protein> getProteinList() {
        return proteinList;
    }
    @Override
    public List<AminoAcid> getAminoAcidList() {
        return aminoAcidList;
    }
    @Override
    public List<RNA> getRNAList() {
        return rnaList;
    }
    @Override
    public List<Nucleotide> getNucleotideList() {
        return nucleotideList;
    }

    @Override
    public String getName() {
        return "Cell Food";
    }

    public void addAllAminoAcids(int aminoAcidFeedCt) {
        for (int i = 0; i < aminoAcidFeedCt; i++) {
            aminoAcidList.add(new AddAminoAcidToProtein());
            aminoAcidList.add(new CopyDNA());
            aminoAcidList.add(new DigestFood());
            aminoAcidList.add(new DivideCell());
            aminoAcidList.add(new EatFood());
            aminoAcidList.add(new FindNextGene());
            aminoAcidList.add(new GetAminoAcidFromCodon());
            aminoAcidList.add(new GetCodonFromRNA());
            aminoAcidList.add(new GetRNAFromGene());
            aminoAcidList.add(new WaitForEnoughProteins());
        }
    }
    public void addAllNucleotides(int nucleotideFeedCt) {
        for (int i = 0; i < nucleotideFeedCt; i++) {
            nucleotideList.add(new Adenine());
            nucleotideList.add(new Cytosine());
            nucleotideList.add(new Guanine());
            nucleotideList.add(new Thymine());
            nucleotideList.add(new Uracil());
        }
    }
}
