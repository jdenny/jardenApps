package jarden.life.nucleicacid;

/*
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
	public char getCode();
	public String getName();
}

enum AminoAcidEnum {
	Lysine, Arginine, Histidine, AsparticAcid, GlutamicAcid,
	Asparagine, Glutamine, Serine, Threonine, Tyrosine,
	Glycine, Alanine, Valine, Leucine, Isoleucine,
	Proline, Phenylalanine, Methionine, Tryptophan, Cysteine,
	Stop;
	
	static AminoAcidEnum[] codonTable = {
		Phenylalanine, Phenylalanine, Leucine,       Leucine,	 // UUU, UUC, UUA, UUG
		Serine,        Serine,        Serine,        Serine,	 // UCU, UCC, UCA, UCG
		Tyrosine,      Tyrosine,      Stop,          Stop,		 // UAU, UAC, UAA, UAG
		Cysteine,      Cysteine,      Stop,          Tryptophan, // UGU, UGC, UGA, UGG
		
		Leucine,       Leucine,       Leucine,       Leucine,	 // CUU, CUC, CUA, CUG
		Proline,       Proline,       Proline,       Proline,	 // CCU, CCC, CCA, CCG
		Histidine,     Histidine,     Glutamine,     Glutamine,	 // CAU, CAC, CAA, CAG
		Arginine,      Arginine,      Arginine,      Arginine,	 // CGU, CGC, CGA, CGG

		Isoleucine,    Isoleucine,    Isoleucine,    Methionine, // AUU, AUC, AUA, AUG
		Threonine,     Threonine,     Threonine,     Threonine,	 // ACU, ACC, ACA, ACG 
		Asparagine,    Asparagine,    Lysine,        Lysine,	 // AAU, AAC, AAA, AAG
		Serine,        Serine,        Arginine,      Arginine,	 // AGU, AGC, AGA, AGG
		
		Valine,        Valine,        Valine,        Valine,	 // GUU, GUC, GUA, GUG
		Alanine,       Alanine,       Alanine,       Alanine,	 // GCU, GCC, GCA, GCG
		AsparticAcid,  AsparticAcid,  GlutamicAcid,  GlutamicAcid,//GAU, GAC, GAA, GAG
		Glycine,       Glycine,       Glycine,       Glycine	 // GGU, GGC, GGA, GGG
	};
}


