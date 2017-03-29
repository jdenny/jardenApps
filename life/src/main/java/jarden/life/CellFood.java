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
import jarden.life.aminoacid.Proline;
import jarden.life.aminoacid.Serine;
import jarden.life.aminoacid.Threonine;
import jarden.life.aminoacid.Tryptophan;
import jarden.life.aminoacid.Tyrosine;
import jarden.life.aminoacid.Valine;
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

    public void addNucleotides() {
        addNucleotides(nucleotideList);
    }
    public static void addNucleotides(List<Nucleotide> nucleotideList) {
        // add extra Adenine, so they don't all get used up for the
        // daughter cell, and thus we can see it's being fed
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
    public void addAminoAcids() {
        addAminoAcids(aminoAcidList);
    }
    public static void addAminoAcids(List<AminoAcid> aminoAcidList) {
        for (int i = 0; i < Cell.aminoAcidFeedCounts[0]; i++) {
            aminoAcidList.add(new Alanine());
        }
        for (int i = 0; i < Cell.aminoAcidFeedCounts[1]; i++) {
            aminoAcidList.add(new Arginine());
        }
        for (int i = 0; i < Cell.aminoAcidFeedCounts[2]; i++) {
            aminoAcidList.add(new Asparagine());
        }
        for (int i = 0; i < Cell.aminoAcidFeedCounts[3]; i++) {
            aminoAcidList.add(new AsparticAcid());
        }
        for (int i = 0; i < Cell.aminoAcidFeedCounts[20]; i++) {
            aminoAcidList.add(new CopyDNA());
        }
        for (int i = 0; i < Cell.aminoAcidFeedCounts[4]; i++) {
            aminoAcidList.add(new Cysteine());
        }
        for (int i = 0; i < Cell.aminoAcidFeedCounts[21]; i++) {
            aminoAcidList.add(new DigestFood());
        }
        for (int i = 0; i < Cell.aminoAcidFeedCounts[22]; i++) {
            aminoAcidList.add(new DivideCell());
        }
        for (int i = 0; i < Cell.aminoAcidFeedCounts[23]; i++) {
            aminoAcidList.add(new EatFood());
        }
        for (int i = 0; i < Cell.aminoAcidFeedCounts[5]; i++) {
            aminoAcidList.add(new GlutamicAcid());
        }
        for (int i = 0; i < Cell.aminoAcidFeedCounts[6]; i++) {
            aminoAcidList.add(new Glutamine());
        }
        for (int i = 0; i < Cell.aminoAcidFeedCounts[7]; i++) {
            aminoAcidList.add(new Glycine());
        }
        for (int i = 0; i < Cell.aminoAcidFeedCounts[8]; i++) {
            aminoAcidList.add(new Histidine());
        }
        for (int i = 0; i < Cell.aminoAcidFeedCounts[9]; i++) {
            aminoAcidList.add(new Isoleucine());
        }
        for (int i = 0; i < Cell.aminoAcidFeedCounts[10]; i++) {
            aminoAcidList.add(new Leucine());
        }
        for (int i = 0; i < Cell.aminoAcidFeedCounts[11]; i++) {
            aminoAcidList.add(new Lysine());
        }
        for (int i = 0; i < Cell.aminoAcidFeedCounts[12]; i++) {
            aminoAcidList.add(new Methionine());
        }
        for (int i = 0; i < Cell.aminoAcidFeedCounts[13]; i++) {
            aminoAcidList.add(new Phenylalanine());
        }
        for (int i = 0; i < Cell.aminoAcidFeedCounts[14]; i++) {
            aminoAcidList.add(new Proline());
        }
        for (int i = 0; i < Cell.aminoAcidFeedCounts[15]; i++) {
            aminoAcidList.add(new Serine());
        }
        for (int i = 0; i < Cell.aminoAcidFeedCounts[16]; i++) {
            aminoAcidList.add(new Threonine());
        }
        for (int i = 0; i < Cell.aminoAcidFeedCounts[17]; i++) {
            aminoAcidList.add(new Tryptophan());
        }
        for (int i = 0; i < Cell.aminoAcidFeedCounts[18]; i++) {
            aminoAcidList.add(new Tyrosine());
        }
        for (int i = 0; i < Cell.aminoAcidFeedCounts[19]; i++) {
            aminoAcidList.add(new Valine());
        }
    }
}
