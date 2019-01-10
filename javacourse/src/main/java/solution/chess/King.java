package solution.chess;

public class King extends ChessPiece {
	public King(boolean white, int xPos, int yPos) {
		super(white, "King", xPos, yPos);
	}
	@Override
	public void validateMove(int x, int y) {
		// must be a valid move for a king, i.e. 1 space in any direction
		if (Math.abs(x - this.getXPos()) > 1 || Math.abs(y - this.getYPos()) > 1) {
			throw new IllegalArgumentException("king can only move 1 square!");
		}
	}
}

