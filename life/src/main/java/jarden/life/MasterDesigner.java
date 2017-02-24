package jarden.life;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MasterDesigner implements CellListener {
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
        syntheticCell.setCellListener(this);
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

    @Override
    public void onCellUpdated(CellData cellData) {
        System.out.println("MasterDesigner.onCellUpdated: " + cellData);
    }

    @Override
    public void onProteinStatusUpdated(int proteinId, String status) {
        System.out.println("MasterDesigner.onProteinStatusUpdated(" +
                proteinId + ", " + status + ")");
    }
}
