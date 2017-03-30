package jarden.life.aminoacid;

/**
 * Created by john.denny@gmail.com on 10/02/2017.
 */

/*
	Current implementation of codonTable. Left column is index.
	See Nucleotide for real-life codonTable.
	0	Alanine                 GCU  // convert DNA codon to RNA codon
	1	Arginine                CGU  // data: RNA
	2	Asparagine              AAU  // data: needMoreFood
	3	AsparticAcid            GAU  // turn on data mode
	20	CopyDNA        			UUG
	4	Cysteine                UGU  // turn on code mode
	21	DigestFood              UCA
	22	DivideCell              UAC
	23	EatFood                 UGC
	5	GlutamicAcid            GAA  // add resource to cell
	6	Glutamine               CAA  // data: readyToDivide
	7	Glycine                 GGU  // run only one of these proteins
	8	Histidine               CAU
	9	Isoleucine              AUU
	10	Leucine        			UUA  // turn on body mode
	11	Lysine                  AAA
	12	Methionine              AUG  // temporarily used by experiment
	13	Phenylalanine          	UUU  // data: Food
	14	Proline                 CCU  // data: Regulator (rna < target)
	15	Serine        			UCU  // loop
		Start                   UGA  // in real life: Stop
		Stop					UAA
	16	Threonine               ACU
	17	Tryptophan              UGG  // wait for resource
	18	Tyrosine                UAU
	19	Valine                  GUU
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

