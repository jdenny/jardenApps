package jarden.life.aminoacid;

/**
 * Created by john.denny@gmail.com on 10/02/2017.
 */

/*
	Current implementation of codonTable.
	See Nucleotide for real-life codonTable.
		Alanine                 GCU  // data: AminoAcid
		Arginine                CGU  // data: RNA
		Asparagine              AAU  // new resource; data: Protein
		AsparticAcid            GAU  // turn on data mode
		CopyDNA        			UUG
		Cysteine                UGU  // turn on code mode
		DigestFood              UCA
		DivideCell              UAC
		EatFood                 UGC
		GlutamicAcid            GAA  // addAminoAcid resource to cell
		Glutamine               CAA
		Glycine                 GGU
		Histidine               CAU
		Isoleucine              AUU
		Leucine        			UUA  // turn on body mode
		Lysine                  AAA
		Methionine              AUG  // temporarily used by experiment
		Phenylalanine          	UUU  // data: Food
		Polymerase             	UUC
		Proline                 CCU  // data: Regulator (rna < target)
		Ribosome       			UCC
		Serine        			UCU  // loop
		Start                   UGA  // in real life: Stop
		Stop					UAA
		Threonine               ACU
		Tryptophan              UGG  // wait for resource
		Tyrosine                UAU
		Valine                  GUU
		WaitForEnoughProteins   UCG
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

