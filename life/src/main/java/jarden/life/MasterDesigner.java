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
