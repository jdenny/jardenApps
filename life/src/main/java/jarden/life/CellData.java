package jarden.life;

import jarden.life.aminoacid.AminoAcidEnum;

/**
 * Created by john.denny@gmail.com on 23/02/2017.
 */

public class CellData {

    // Count for each protein type
    public class ProteinNameCt {
        String proteinName;
        int proteinCt; // how many of these proteins contained in cell
    }
    int cellId;
    ProteinNameCt[] proteinNameCts;
    public static String[] aminoAcidNames = {
            "AddAminoAcidToProtein",
            "DigestFood", "DivideCell",
            "FindNextGene", "GetAminoAcidFromCodon",
            "GetCodonFromRNA", "GetRNAFromGene"
    };

    /*
    How many of each aminoAcid in cell; array in same order
    as aminoAcidNames;
      */
    int[] aminoAcidCts;
    public static String[] nucleotideNames = {
            "Adenine", "Cytosine", "Guanine", "Thymine", "Uracil",
    };
    /*
    how many of each nucleotide in cell; order of array
    is same as nucleotideNames above
      */
    int[] nucleotideCts;
}
