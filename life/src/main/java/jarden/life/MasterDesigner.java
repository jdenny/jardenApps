package jarden.life;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MasterDesigner implements CellListener {
    private static List<Cell> cellList = new ArrayList<>();
	
	public static void main(String[] args) throws InterruptedException {
        new MasterDesigner();
    }
    public MasterDesigner() throws InterruptedException {

        Cell syntheticCell = Cell.getSyntheticCell();
        syntheticCell.setCellListener(this);
        cellList.add(syntheticCell);

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			char c;
			while (true) {
				System.out.println("p(rint) or q(uit): ");
				c = reader.readLine().charAt(0);
				if (c == 'p') {
                    for (Cell cell: cellList) cell.printCell();
                    System.out.println("cellList.size=" + cellList.size());
                }
				else if (c == 'q') System.exit(0);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    @Override
    public void onNewCell(Cell cell) {
        cellList.add(cell);
        System.out.println("test if perfect copy of first cell: " +
                cell.isCopy(cellList.get(0)));
        System.out.println("cellList.size=" + cellList.size());
    }

    @Override
    public void onCellUpdated(int cellId) {
        System.out.println("MasterDesigner.onCellUpdated: " + cellId);
    }

    @Override
    public void onProteinStatusUpdated(int proteinId, String status) {
        System.out.println("MasterDesigner.onProteinStatusUpdated(" +
                proteinId + ", " + status + ")");
    }
}
