package jarden.life;

import java.util.List;

import jarden.life.aminoacid.AminoAcid;
import jarden.life.nucleicacid.DNA;
import jarden.life.nucleicacid.Nucleotide;
import jarden.life.nucleicacid.RNA;

/**
 * Created by john.denny@gmail.com on 20/02/2017.
 */

public interface Food extends CellResource {
    // TODO: could have partial digestion, that breaks down
    // proteins into aminoAcids, DNA & RNA into nucleotides
    //!! DNA getDNA();
    //!! List<Protein> getProteinList();
    List<AminoAcid> getAminoAcidList();
    //!! List<RNA> getRNAList();
    List<Nucleotide> getNucleotideList();
}
