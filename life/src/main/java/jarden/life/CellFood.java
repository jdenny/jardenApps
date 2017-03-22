package jarden.life;

import java.util.LinkedList;
import java.util.List;

import jarden.life.aminoacid.Alanine;
import jarden.life.aminoacid.AminoAcid;
import jarden.life.aminoacid.Arginine;
import jarden.life.aminoacid.Asparagine;
import jarden.life.aminoacid.AsparticAcid;
import jarden.life.aminoacid.CopyDNA;
import jarden.life.aminoacid.Cysteine;
import jarden.life.aminoacid.DigestFood;
import jarden.life.aminoacid.DivideCell;
import jarden.life.aminoacid.EatFood;
import jarden.life.aminoacid.GlutamicAcid;
import jarden.life.aminoacid.Glutamine;
import jarden.life.aminoacid.Glycine;
import jarden.life.aminoacid.Histidine;
import jarden.life.aminoacid.Isoleucine;
import jarden.life.aminoacid.Leucine;
import jarden.life.aminoacid.Lysine;
import jarden.life.aminoacid.Methionine;
import jarden.life.aminoacid.Phenylalanine;
import jarden.life.aminoacid.Polymerase;
import jarden.life.aminoacid.Proline;
import jarden.life.aminoacid.Ribosome;
import jarden.life.aminoacid.Serine;
import jarden.life.aminoacid.Threonine;
import jarden.life.aminoacid.Tryptophan;
import jarden.life.aminoacid.Tyrosine;
import jarden.life.aminoacid.Valine;
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
        addAminoAcids(aminoAcidList);
    }
    public static void addAminoAcids(List<AminoAcid> aminoAcidList) {
        aminoAcidList.add(new Alanine());
        aminoAcidList.add(new Arginine());
        aminoAcidList.add(new Asparagine());
        aminoAcidList.add(new Asparagine());
        aminoAcidList.add(new AsparticAcid());
        aminoAcidList.add(new CopyDNA());
        aminoAcidList.add(new Cysteine());
        aminoAcidList.add(new DigestFood());
        aminoAcidList.add(new DivideCell());
        aminoAcidList.add(new EatFood());
        aminoAcidList.add(new GlutamicAcid());
        aminoAcidList.add(new Glutamine());
        aminoAcidList.add(new Glycine());
        aminoAcidList.add(new Histidine());
        aminoAcidList.add(new Isoleucine());
        aminoAcidList.add(new Leucine());
        aminoAcidList.add(new Lysine());
        aminoAcidList.add(new Methionine());
        aminoAcidList.add(new Phenylalanine());
        aminoAcidList.add(new Polymerase());
        aminoAcidList.add(new Proline());
        aminoAcidList.add(new Ribosome());
        aminoAcidList.add(new Serine());
        aminoAcidList.add(new Threonine());
        aminoAcidList.add(new Tryptophan());
        aminoAcidList.add(new Tyrosine());
        aminoAcidList.add(new Valine());
        aminoAcidList.add(new WaitForEnoughProteins());
        for (int i = 0; i < 4; i++) {
            aminoAcidList.add(new AsparticAcid());
            aminoAcidList.add(new Cysteine());
            aminoAcidList.add(new Tryptophan());
        }
    }
    public void addNucleotides() {
        addNucleotides(nucleotideList);
    }
    public static void addNucleotides(List<Nucleotide> nucleotideList) {
        for (int i = 0; i < Cell.nucleotideFeedCounts[0] + 1; i++) {
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
}
