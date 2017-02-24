package jarden.life.aminoacid;

/**
 * Created by john.denny@gmail.com on 10/02/2017.
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

