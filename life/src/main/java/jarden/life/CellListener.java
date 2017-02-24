package jarden.life;

/**
 * Created by john.denny@gmail.com on 17/02/2017.
 */

public interface CellListener {
    void onNewCell(Cell cell);
    void onCellUpdated(CellData cellData);
    void onProteinStatusUpdated(int proteinId, String status);
}
