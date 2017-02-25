package jarden.life;

import jarden.life.aminoacid.AminoAcidEnum;

/**
 * Created by john.denny@gmail.com on 23/02/2017.
 */

public class CellData {

    // Count for each protein type
    public static class ProteinNameCount {
        public String name;
        public int count; // how many of these proteins contained in cell
        public ProteinNameCount(String name, int count) {
            this.name = name;
            this.count = count;
        }
    }
    public int cellId;
    public ProteinNameCount[] proteinNameCts;
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
    public int[] aminoAcidCts;
    public static String[] nucleotideNames = {
            "Adenine", "Cytosine", "Guanine", "Thymine", "Uracil",
    };
    /*
    how many of each nucleotide in cell; order of array
    is same as nucleotideNames above
      */
    public int[] nucleotideCts;
}
