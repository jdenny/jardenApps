package jarden.life;

import java.util.List;

import jarden.life.aminoacid.AminoAcid;
import jarden.life.nucleicacid.DNA;
import jarden.life.nucleicacid.Nucleotide;

/**
 * Created by john.denny@gmail.com on 20/02/2017.
 */

public interface Food {
    DNA getDNA();
    List<Protein> getProteinList();
    List<AminoAcid> getAminoAcidList();
    List<Nucleotide> getNucleotideList();
}