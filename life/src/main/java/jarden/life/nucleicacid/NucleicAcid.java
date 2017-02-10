package jarden.life.nucleicacid;

/*
 * A chain of nucleotides. Chain can be either DNA or RNA.
 */
public interface NucleicAcid {
	String promoterCode = "TATAAT";
	String terminatorCode = "TAA";

    String proteinTypeStem = "AAA";
    String proteinTypeDivision = "AAC";
    String proteinTypeDigestion = "AAG";

    String proteinNamePolymerase = "AGA";
    String proteinNameRibosome = "AGC";
    String proteinNameDivide = "AGG";
    String proteinNameDigest = "AGT";
}
