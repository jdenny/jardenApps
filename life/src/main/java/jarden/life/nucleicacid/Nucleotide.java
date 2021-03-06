package jarden.life.nucleicacid;

import jarden.life.CellResource;

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
public interface Nucleotide extends CellResource {
    String stopCode = "TAA";
    String startCode = "TGA"; // if this changes,
    int startLength = startCode.length();
    // also change counts below
    int startAdenineCt = 1;
    int startCytosineCt = 0;
    int startGuanineCt = 1;
    int startThymineCt = 1;

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



