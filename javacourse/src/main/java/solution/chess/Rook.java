package solution.chess;

public class Rook extends ChessPiece {
	public Rook(boolean white, int xPos, int yPos) {
		super(white, "Rook", xPos, yPos);
	}
	@Override
	public void validateMove(int x, int y) {
		// must be a valid move for a rook, i.e. same column or same row
		if (x != this.getXPos() && y != this.getYPos()) {
			throw new IllegalArgumentException("rook can only move along a column or row!");
		}
	}
}

