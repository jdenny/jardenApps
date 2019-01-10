package solution.chess;

public class Knight extends ChessPiece {
	public Knight(boolean white, int xPos, int yPos) {
		super(white, "Knight", xPos, yPos);
	}
	@Override
	public void validateMove(int x, int y) {
		// must be a valid move for a knight, i.e. x + y displacement = 3
		if ((Math.abs(x - this.getXPos()) + Math.abs(y - this.getYPos())) != 3) {
			throw new IllegalArgumentException("knight can only move one straight & one diagonal");
		}
	}
	@Override
	public String getShortName() {
		return "N";
	}
}

