package jarden.life;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MasterDesigner {
    private static List<Cell> cellList;
	
	public static void main(String[] args) throws InterruptedException {
        new MasterDesigner();
    }
    public MasterDesigner() throws InterruptedException {
        CellEnvironment cellEnvironment = new CellEnvironment(true);
        cellList = cellEnvironment.getCellList();

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			char c;
			while (true) {
				System.out.println("p(rint) or q(uit): ");
				c = reader.readLine().charAt(0);
				if (c == 'p') {
                    for (Cell cell: cellList) {
                        CellData cellData = cell.getCellData();
                        System.out.println(cellData);
                    }
                    System.out.println("cellList.size=" + cellList.size());
                }
				else if (c == 'q') System.exit(0);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
