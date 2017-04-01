package jarden.life.aminoacid;

/**
 * Created by john.denny@gmail.com on 10/02/2017.
 */

/*
	Current implementation of codonTable. * means free to use; next column is index.
	See Nucleotide for real-life codonTable.
F	0	"GCT" + // GCU, Alanine
F	1	"CGT" + // CGU, Arginine, set nucleotide type to RNA; data: RNA
	2	"AAT" + // AAU, Asparagine, data: needMoreFood
	3	"GAT" + // GAU, AsparticAcid, turn on data mode
	20	"TTG" + // UUG, CopyDNA
	4	"TGT" + // UGU, Cysteine, turn on code mode
	21	"TCA" + // UCA, DigestFood
	22	"TAC" + // UAC, DivideCell
	5	"GAA" + // GAA, GlutamicAcid, add resource to cell
F	6	"CAA" + // CAA, Glutamine, data: readyToDivide
	7	"GGT" + // GGU, Glycine, run only one of these proteins
F	8	"CAT" + // CAU, Histidine, data: food from environment
F	9	"ATT" + // AUU, Isoleucine, set nucleotide type to DNA; data: DNA
	10	"TTA" + // UUA, Leucine, turn on body mode
F	11	"AAA" + // AAA, Lysine
F	12	"ATG" + // AUG, Methionine
	13	"TTT" + // UUU, Phenylalanine, data: Food
F	14	"CCT" + // CCU, Proline, data: Regulator (rna < target)
	15	"TCT" + // UCU, Serine, loop
		"TGA" + // UGA, Start (in real life: Stop!)
		"TAA" + // UAA, Stop
F	16	"ACT" + // ACU, Threonine
	17	"TGG" + // UGG, Tryptophan, wait for resource
F	18	"TAT" + // UAU, Tyrosine
F	19	"GTT" + // GUU, Valine
	                            UCG
		                        UAG
		                        CUU
		                        CUC
		                        CUA
		                        CUG
		                        CCC
		                        CCA
		                        CCG
		                        CAC
		                        CAG
		                        CGC
		                        CGA
		                        CGG
		                        AUC
		                        AUA
		                        ACC
		                        ACA
		                        ACG
		                        AAC
		                        AAG
		                        AGU
		                        AGC
		                        AGA
		                        AGG
		                        GUC
		                        GUA
		                        GUG
		                        GCC
		                        GCA
		                        GCG
		                        GAC
		                        GAG
		                        GGC
		                        GGA
		                        GGG
 */

public enum AminoAcidEnum {
    Alanine,       // A, Ala, {GCU, GCC, GCA, GCG}
    Cysteine,      // C, Cys, {UGU, UGC}
    AsparticAcid,  // D, Asp, {GAU, GAC}
    GlutamicAcid,  // E, Glu, {GAA, GAG}
    Phenylalanine, // F, Phe, {UUU, UUC}
    Glycine,       // G, Gly, {GGU, GGC, GGA, GGG}
    Histidine,     // H, His, {CAU, CAC}
    Isoleucine,    // I, Ile, {AUU, AUC, AUA}
    Lysine,        // K, Lys, {AAA, AAG}
    Leucine,       // L, Leu, {UUA, UUG, CUU, CUC, CUA, CUG}
    Methionine,    // M, Met, {AUG}
    Asparagine,    // N, Asn, {AAU, AAC}
    Proline,       // P, Pro, {CCU, CCC, CCA, CCG}
    Glutamine,     // Q, Gln, {CAA, CAG}
    Arginine,      // R, Arg, {CGU, CGC, CGA, CGG, AGA, AGG}
    Serine,        // S, Ser, {UCU, UCC, UCA, UCG, AGU, AGC}
    Threonine,     // T, Thr, {ACU, ACC, ACA, ACG}
    Valine,        // V, Val, {GUU, GUC, GUA, GUG}
    Tryptophan,    // W, Trp, {UGG}
    Tyrosine,      // Y, Tyr, {UAU, UAC}

    Stop;          //         {UAA, UAG, UGA}

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

