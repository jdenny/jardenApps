package jarden.life.nucleicacid;

/**
 * Nucleotide: basic unit of information in life.
 * DNA nucleotides: 4 types, made of
 *    a base: adenine (A), thymine (T), cytosine (C) or guanine (G)
 *    a deoxyribose sugar, and a phosphate
 *    bonds: A - T; C - G
 * RNA nucleotides: 4 types, made of
 *    a nitrogenous base: adenine (A), uracil (U), cytosine (C) or guanine (G)
 *    a ribose sugar, and a phosphate
 *    bonds: A - U; C - G
 */
public interface Nucleotide {
    String promoterCode = "TATAAT"; // if this changes,
    int promoterLength = promoterCode.length();
    // also change counts below
    String terminatorCode = "TAA";
    int promoterThymineCt = 3;
    int promoterAdenineCt = 4;

    /**
     * Tests if this nucleotide is suitable to form a DNA base-pair
     * bond with otherNucleotide. In DNA: Adenine bonds with Thymine;
     * Cytosine bonds with Guanine.
     *
     * @param otherNucleotide to form a DNA base-pair bond with
     * @return true if suitable for DNA base-pair bond
     */
    boolean dnaMatch(Nucleotide otherNucleotide);

    /**
     * Tests if this nucleotide is suitable to form an RNA base-pair
     * bond with otherNucleotide. In RNA: Adenine bonds with Uracil;
     * Cytosine bonds with Guanine.
     *
     * Adenine bonds with Thymine; Cytosine bonds with Guanine.
     * @param otherNucleotide to form a RNA base-pair bond with
     * @return true if suitable for RNA base-pair bond
     */
    boolean rnaMatch(Nucleotide otherNucleotide);

    char getCode(); // first letter of name
	String getName();
    int getIndex(); // index to array
}



