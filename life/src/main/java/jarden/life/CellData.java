package jarden.life;

import jarden.life.aminoacid.AminoAcidEnum;

/**
 * Created by john.denny@gmail.com on 23/02/2017.
 */

public class CellData {
    public static String[] aminoAcidNames = {
            /*!!
            "AddAminoAcidToProtein",
            "DigestFood", "DivideCell", "EatCell",
            "FindNextGene", "GetAminoAcidFromCodon",
            "GetCodonFromRNA", "GetRNAFromGene"
            */
            "AddAminoAcidToProtein", "CopyDNA",
            "DigestFood", "DivideCell", "EatFood",
            "GetAminoAcidFromCodon", "GetCodonFromRNA",
            "Polymerase", "WaitForEnoughProteins"
    };
    public static String[] nucleotideNames = {
            "Adenine", "Cytosine", "Guanine", "Thymine", "Uracil",
    };
    // Count for each protein type
    public static class ProteinNameCount {
        public String name;
        public int count; // how many of these proteins contained in cell
        public ProteinNameCount(String name, int count) {
            this.name = name;
            this.count = count;
        }
        public String toString() {
            return count + " " + name;
        }
    }

    public int cellId;
    public ProteinNameCount[] proteinNameCts;
    /*
    How many of each aminoAcid in cell; array in same order
    as aminoAcidNames;
      */
    public int[] aminoAcidCts;
    /*
    how many of each nucleotide in cell; order of array
    is same as nucleotideNames above
      */
    public int[] nucleotideCts;

    public String toString() {
        return "cellData; id=" + cellId;
    }
    public void printIt() {
        System.out.println(this.toString());
        for (ProteinNameCount proteinNameCount: proteinNameCts) {
            System.out.println(proteinNameCount);
        }
    }
}
