package solution.chess;

import jarden.gui.Chess;
import jarden.gui.ChessSwing;

public class ChessMain {

	public static void main(String[] args) {
		Chess[] chessPieces = {
			new King(true, 5, 1),
			new Rook(true, 1, 1),
			new Knight(true, 2, 1)
		};
		new ChessSwing(chessPieces);
	}

}
