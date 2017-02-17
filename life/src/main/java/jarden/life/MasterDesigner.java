package jarden.life;

import static jarden.life.nucleicacid.NucleicAcid.promoterCode;
import static jarden.life.nucleicacid.NucleicAcid.proteinNameDigest;
import static jarden.life.nucleicacid.NucleicAcid.proteinNameDivide;
import static jarden.life.nucleicacid.NucleicAcid.proteinNamePolymerase;
import static jarden.life.nucleicacid.NucleicAcid.proteinNameRibosome;
import static jarden.life.nucleicacid.NucleicAcid.proteinTypeDigestion;
import static jarden.life.nucleicacid.NucleicAcid.proteinTypeDivision;
import static jarden.life.nucleicacid.NucleicAcid.proteinTypeStem;
import static jarden.life.nucleicacid.NucleicAcid.terminatorCode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import jarden.life.aminoacid.*;
import jarden.life.nucleicacid.Adenine;
import jarden.life.nucleicacid.Cytosine;
import jarden.life.nucleicacid.DNA;
import jarden.life.nucleicacid.Guanine;
import jarden.life.nucleicacid.Uracil;

public class MasterDesigner implements OnNewCellListener {
	private static boolean verbose = true;
    private static List<Cell> cellList = new ArrayList<>();
	
	public static void print(String s) {
		if (verbose) {
			System.out.println(s);
		}
	}
	
	public static void main(String[] args) {
        new MasterDesigner();
    }
    public MasterDesigner() {

        Cell syntheticCell = Cell.getSyntheticCell();
        syntheticCell.setOnNewCellListener(this);
        cellList.add(syntheticCell);

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			char c;
			while (true) {
				System.out.println("p(rint) or q(uit) or v(erbose): ");
				c = reader.readLine().charAt(0);
				if (c == 'p') {
                    for (Cell cell: cellList) cell.printCell();
                    System.out.println("cellList.size=" + cellList.size());
                }
				else if (c == 'q') System.exit(0);
				else if (c == 'v') verbose = !verbose;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    @Override
    public void onNewCell(Cell cell) {
        cellList.add(cell);
        System.out.println("test for equality with first cell: " +
                cell.equals(cellList.get(0)));
        System.out.println("cellList.size=" + cellList.size());
    }
}

        /*
        Next step options:
            synthetic cell has 4 build proteins, plus resources
            (DNA, aminoAcids & nucleotides) for 4 more build proteins
            this should run, now with total of 8 proteins, and split;
            daughterCell is now first real cell
            test that granddaughter cell is identical to daughter cell
        Okay, what prompts a protein into action?
         ribosome, because some RNA becomes available;
         digest, because some food (cells) becomes available
         divide, because enough proteins & nucleicAcid available
         polymerase, because some nucleicAcid is available
         Note: in future, could activate a protein because a
            resource has become scarce,
            e.g. if level < target then wait()

        Cell firstCell = Cell.makeFirstCell(); // cell should now be running!
        now we just watch it, and feed it!

        so...
        digest breaks down parts of digest cells to build nucleotides & aminoAcids
        when sufficient of above, build proteins
        when sufficient proteins, split cell

        Next steps:
            move buildDNAFromString, dnaString to Cell
        static methods:
         Cell Cell.getSyntheticCell(); // use static DNA; add build proteins
         Cell Cell.makeFirstCell() {
             lazy evaluate syntheticCell
             return cell.split(), i.e. copy DNA; build all proteins;
                move proteins to new cell; start all proteins if activateOnCreate;
        cell.proteinAction(String proteinName); // call polymerase to get single gene
            // unless protein already exists, in which case start its action
        cell.groupAction(String proteinTypeName);
        use new gene structure:
            promoterCode, 2 codons for protein type
             (e.g. "stem", "digestion", "division"),
            2 codons for protein name (e.g. "polymerase"), terminatorCode
            all this can be decoded in FindNextGene?
            now can have protein: turnOn/Off protein(s) by name or type
            cell needs method runProtein(name/type); as described below, all
            proteins run in own thread, but some are chains, so wait, some are
            not so only run once. Cell.runProtein could run protein that activates
            a group of proteins
        protein(s) to split a cell
        proteins to digest a cell; user action to 'feed' a cell

        new cell should have proteins polymerase & ribosome
        plus DNA for all proteins

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


        something must tell the cell which proteins to build; look in book
        Is there is an inhibitor on the DNA that stops the transcription?
        protein to digest cells, i.e. convert proteins into aminoAcid
            and add to cell.aminoAcidList, and convert RNA & DNA into nucleotides,
            and add to cell.nucleotides
         */

