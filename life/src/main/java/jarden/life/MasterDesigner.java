package jarden.life;

import static jarden.life.nucleicacid.NucleicAcid.promoterCodes;
import static jarden.life.nucleicacid.NucleicAcid.terminatorCodes;
import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import jarden.life.aminoacid.*;
import jarden.life.nucleicacid.Adenine;
import jarden.life.nucleicacid.Cytosine;
import jarden.life.nucleicacid.DNA;
import jarden.life.nucleicacid.Guanine;
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
        Latest thoughts: proposed structure of gene:
            promoterCodes, 2 codons for protein type (e.g. "stem", "digestion"),
            2 codons for protein name (e.g. "polymerase"), terminatorCodes
            all this can be decoded in GetGeneFromDNA?
            now can have protein: turnOn/Off protein(s) by name or type
            cell needs method runProtein(name/type); as described below, all
            proteins run in own thread, but some are chains, so wait, some are
            not so only run once. Cell.runProtein could run protein that activates
            a group of proteins

        What do we want to happen?
        Key requirement: need protein that triggers building of a specific
        protein, or group of proteins.
        1. Manually create cell with DNA to build 4 proteins:
           rnaPolymerase & ribosome, both in group "BuildProteins"
           plus 2 simple proteins (to be decided!), both in group "SimpleLife"
        2. Manually create BuildProteins and add to cell
           **** test correct proteins & DNA
        3. cell.action() which should run the 2 proteins; the first (rnaPolymerase) should
           create 2 RNA chains and add them to the cell; the second (ribosome)
           should process both elements in cell.rnaList, and for each create 1 more protein
           **** test there are now 4 proteins, each with correct aminoAcids
        4. split the cell, to create 2 cells: 1st as for steps 1 & 2; 2nd with DNA
           for 2 simple proteins, plus 2 proteins created in step 3
           *** test first cell has 2 correct proteins & DNA
           *** test second cell has 2 correct proteins & DNA


        4. stop the second protein (ribosome); remove the first two proteins
           cell.actions(), i.e. run the 2 cell-produced proteins; should now be 4

        What happened? there are now 4 proteins, polymerase, ribosome, ribosome, polymerase
        strange order is because Cell.getRNA() gets last one first, as this is more
        efficient use of arraylist. As it happens, we can still disable hand-built proteins
        (but don't forget to stop the thread that is already running!), then run the new
        ribosome first, which will then wait for rna, then run polymerase, which should
        provide it!
        4. switch off the 1st 2 proteins; cell.action() should run the 2 proteins built
           by the cell; should now be 6


        Questions: what activates proteins? what inhibits genes?

        What after this?
        need a protein to turn on/off a gene:
          find specified gene; change promoter code from "TATAAT" to "UATAAT" for off
          or vice versa for on; while we're waiting for this protein, do it manually!

        start splitting aminoAcids into smaller units, e.g. isChain(), hasMore() could
        be replaced by aminoAcid that simply sets a property on the protein: boolean isChain
        perhaps all proteins could run in their own thread? (cf keepRunning)


        something must tell the cell which proteins to build; look in book
        Is there is an inhibitor on the DNA that stops the transcription?
        protein to digest cells, i.e. convert proteins into aminoAcid
            and add to cell.aminoAcidList, and convert RNA & DNA into nucleotides,
            and add to cell.nucleotides
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
		Cell syntheticCell = new Cell(dna);
        Protein rnaPolymerase = new Protein();
        rnaPolymerase.add(new GetGeneFromDNA(syntheticCell));
        rnaPolymerase.add(new GetRNAFromGene(syntheticCell));
		Protein ribosome = new Protein();
		ribosome.add(new GetCodonFromRNA(syntheticCell));
		ribosome.add(new GetAminoAcidFromCodon(syntheticCell));
		ribosome.add(new AddAminoAcidToProtein(syntheticCell));
		syntheticCell.addProtein(rnaPolymerase);
        syntheticCell.addProtein(ribosome);
        // so now we have cell with 2 hand-built proteins, which
        // between them can build proteins from DNA
        // *** this is the proper test:
        Cell daughterCell = syntheticCell.split();
        // test for same DNA & same protons:
        // assertEquals(syntheticCell, daughterCell);
        System.out.println("***syntheticCell");
        syntheticCell.printCell();
        System.out.println("***daughterCell");
        daughterCell.printCell();
        // TODO: replace above 2 lines with assertEquals
        // final confirmation, but I don't see how this can fail
        // if the above test succeeds! How wrong could I be!
        // assertEquals(syntheticCell, daughterCell.split());
        System.out.println("***grandDaughter");
        daughterCell.split().printCell();

        // *** end of proper test
        /*
        // test cell:
        assertEquals(dnaStr, syntheticCell.getDNA().toString());
        List<Protein> proteinList = syntheticCell.getProteins();
        assertEquals(2, proteinList.size());
        assertEquals(rnaPolymerase, proteinList.get(0));
        assertEquals(ribosome, proteinList.get(1));
        syntheticCell.runProtein("rnaPlymerase"); // TODO: maybe some hashcode?
        syntheticCell.waitForProtein("ribosome"); // i.e. await completion
        // TODO: test cell now has correct 4 proteins
        Cell cell2 = syntheticCell.split();
        */

        // run both proteins; first protein should add 2 rna objects to cell;
        // second protein, running in its own thread, should process both these
        // rna objects to produce 2 new proteins; 2nd thread won't run until
        // main thread yields, e.g. wait for user typing
        /*
        syntheticCell.action(null);
        Thread.yield(); // allow ribosome to do its job
        cell.printProteins();
        */
        boolean stop = true;
        if (stop) {
            System.out.println("stop is true");
            return;
        }
        /*
        // if this all works, disable 1st 2 proteins, cell.action(null), should be 6
        // proteins!
        cell.action(null); // run both proteins, which should add ribosome protein to cell
        cell.printRNA(); cell.printProteins();
        cell.action(null);
        */

        // add new option to tell cell to stop protein[1], delete proteins[0 & 1],
        // then cell.action()

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			char c;
			while (true) {
				System.out.println("b(odge) or c or g or a or u or p(rint) or q(uit) or v(erbose): ");
				c = reader.readLine().charAt(0);
				if (c == 'A') syntheticCell.addNucleotide(new Adenine());
                else if (c == 'b') syntheticCell.bodge();
                else if (c == 'c') syntheticCell.addNucleotide(new Cytosine());
				else if (c == 'g') syntheticCell.addNucleotide(new Guanine());
				else if (c == 'a') syntheticCell.addNucleotide(new Adenine());
				else if (c == 'u') syntheticCell.addNucleotide(new Uracil());
				else if (c == 'p') syntheticCell.printCell();
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
