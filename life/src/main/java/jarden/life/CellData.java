package jarden.life;

/**
 * Created by john.denny@gmail.com on 23/02/2017.
 */

public class CellData {
    public static String[] aminoAcidNames = {
            "CopyDNA", "DigestFood", "DivideCell", "EatFood",
            "Polymerase", "Ribosome", "WaitForEnoughProteins"
    };
    public static String[] nucleotideNames = {
            "Adenine", "Cytosine", "Guanine", "Thymine", "Uracil",
    };

    public int cellId;
    public int rnaCt; // number of rna strands
    public NameCount[] proteinNameCts;
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
        for (NameCount proteinNameCount: proteinNameCts) {
            System.out.println(proteinNameCount);
        }
    }
}
