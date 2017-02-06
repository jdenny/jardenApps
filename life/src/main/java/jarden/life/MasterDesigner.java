package jarden.life;

import static jarden.life.nucleicacid.NucleicAcid.promoterCodes;
import static jarden.life.nucleicacid.NucleicAcid.terminatorCodes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import jarden.life.aminoacid.*;
import jarden.life.nucleicacid.Adenine;
import jarden.life.nucleicacid.Cytosine;
import jarden.life.nucleicacid.DNA;
import jarden.life.nucleicacid.Guanine;
import jarden.life.nucleicacid.Thymine;
import jarden.life.nucleicacid.Uracil;

public class MasterDesigner {
	private static boolean verbose = true;
	
	public static void print(String s) {
		if (verbose) {
			System.out.println(s);
		}
	}
	
	public static void main(String[] args) {
        /*
        What do we want to happen?
        Manually create cell with DNA to build 2 proteins:
          rnaPolymerase & ribosome, i.e. enough to build proteins from DNA!
        Manually create above 2 proteins and add to cell
        cell.action() which should run the 2 proteins, which should create 1 more protein
        cell.action() which should run the 3 proteins, which should create 1 more protein
        cell.action() which should run the 3 proteins, which should create 1 more protein
            and leave some RNA hanging around!
        cell.action() which should run the 4 proteins and create 2 more proteins

        What did happen? as expected

        What next? make rnaPolymerase into a chain, so it produces both RNA strands from the DNA
            then cell.action() should run the 2 proteins to create 2 more proteins

        What after this?
        something must tell the cell which proteins to build; look in book
        Is there is an inhibitor on the DNA that stops the transcription?
         */

		/*
		In this pseudo DNA are 3 genes to make the following proteins:
		polymerase:              (RNA codons)
		   GetGeneFromDNA        (UUG)
		   GetRNAFromGene        (UCU)
		   Stop                  (UAA)
		ribosome:
		   GetCodonFromRNA       (UUA)
		   GetAminoAcidFromCodon (UUC)
		   AddAminoAcidToProtein (UUU)
		   Stop                  (UAA)
		newUracil: CreateUracil - not currently used!
		 */
		String dnaStr =
            promoterCodes + "TTGTCT" + terminatorCodes
			+ promoterCodes + "TTATTCTTT" + terminatorCodes;
			// + promoterCodes + "TCC" + terminatorCodes;
		DNA dna = GetGeneFromDNA.buildDNAFromString(dnaStr);
		Cell cell = new Cell(dna);
        Protein rnaPolymerase = new Protein();
        rnaPolymerase.add(new GetGeneFromDNA(cell));
        rnaPolymerase.add(new GetRNAFromGene(cell));
		Protein ribosome = new Protein();
		ribosome.add(new GetCodonFromRNA(cell));
		ribosome.add(new GetAminoAcidFromCodon(cell));
		ribosome.add(new AddAminoAcidToProtein(cell));
		cell.addProtein(rnaPolymerase);
        cell.addProtein(ribosome);
        // so now we have cell with 2 hand-built proteins, which
        // between them can build proteins from DNA
        cell.printRNA(); cell.printProteins();
        cell.action(null); // run both proteins, which should add rnaPolymerase protein to cell
        cell.printRNA(); cell.printProteins();
        cell.action(null); // run both proteins, which should add ribosome protein to cell
        cell.printRNA(); cell.printProteins();
        cell.action(null);

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			char c;
			while (true) {
				System.out.println("c or g or a or u or p(rint) or q(uit) or v(erbose): ");
				c = reader.readLine().charAt(0);
				if (c == 'A') cell.addNucleotide(new Adenine());
				else if (c == 'c') cell.addNucleotide(new Cytosine());
				else if (c == 'g') cell.addNucleotide(new Guanine());
				else if (c == 'a') cell.addNucleotide(new Adenine());
				else if (c == 'u') cell.addNucleotide(new Uracil());
				else if (c == 'p') cell.printCell();
				else if (c == 'q') System.exit(0);
				else if (c == 'v') verbose = !verbose;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*
		for (Object builtObject: objectList) {
			System.out.println("Object produced " + builtObject);
			if (builtObject instanceof Protein) {
				Protein builtProtein = (Protein)builtObject;
				ArrayList gen2ObjectList = builtProtein.action(rna);
				for (Object gen2BuiltObject: gen2ObjectList) {
					System.out.println("2nd generation object produced " + gen2BuiltObject);
				}
			}
		}
		*/
	}
}
