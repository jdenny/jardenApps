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
	/*
	 * Uracil('U'), Cytosine('C'), Adenine('A'), Guanine('G'), Thymine('T');
	 */
	char getCode();
	String getName();
}



