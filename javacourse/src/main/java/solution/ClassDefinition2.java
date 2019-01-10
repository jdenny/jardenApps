package solution;

import jarden.gui.ConsoleSwing;
import solution.chess.ChessPiece;
import solution.chess.King;
import solution.chess.Knight;
import solution.chess.Rook;

public class ClassDefinition2 {

	public static void main(String[] args) {
		ChessPiece[] myPieces = {
				new Knight(true, 2, 1),
				new King(true, 4, 1),
				new Rook(true, 1, 1),
				new King(false, 4, 8)
		};
		ConsoleSwing console = new ConsoleSwing();
		console.println("All pieces:");
		int kingCt = 0;
		for (int i = 0; i < myPieces.length; i++) {
			ChessPiece piece = myPieces[i];
			if (piece instanceof King) ++kingCt;
			console.println((i+1) + "  " + piece);
		}
		console.println("Number of kings: " + kingCt);
		int index = console.getInt("choose a piece to move (1 to " + myPieces.length + "):");
		ChessPiece piece = myPieces[index - 1];
		console.println(piece.toString());
		while (true) {
			int newX = console.getInt("supply a new X position: ");
			int newY = console.getInt("supply a new Y position: ");
			try {
				piece.move(newX, newY);
			} catch(IllegalArgumentException e) {
				console.println(e.getMessage());
			}
			console.println(piece.toString());
			String another = console.getString("another move? (y/n)");
			if (!another.startsWith("y")) break; 
		}
		System.out.println("Good move!");
		console.close();
	}
}


