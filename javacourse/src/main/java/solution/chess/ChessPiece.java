package solution.chess;

import jarden.gui.Chess;

import java.text.MessageFormat;

public abstract class ChessPiece implements Chess {
	private final boolean white;
	private final String name;
	private int xPos; // 1 is left-most column from white's point of view
	private int yPos; // 1 is nearest row to white
	
	public ChessPiece(boolean white, String name, int xPos, int yPos) {
		validatePos(xPos, yPos);
		this.white = white;
		this.name = name;
		this.xPos = xPos;
		this.yPos = yPos;
	}
	public String getName() {
		return name;
	}
	/**
	 * Default shortName is 1st char of name; some classes
	 * will need to override this (e.g. King or Knight).
	 */
	public String getShortName() {
		return name.substring(0, 1);
	}
	public int getXPos() {
		return this.xPos;
	}
	public int getYPos() {
		return this.yPos;
	}
	public void move(int x, int y) {
		// must be on the board:
		validatePos(x, y);
		// must be a different position:
		if (x == this.xPos && y == this.yPos) {
			throw new IllegalArgumentException("move is to the same place!");
		}
		validateMove(x, y);
		this.xPos = x;
		this.yPos = y;
	}
	private void validatePos(int x, int y) {
		// in proper solution, need to check what else is in the way!
		// for now, act as if this is the only piece on the board.
		if (x < 1 || x > 8 || y < 1 || y > 8) {
			String message = MessageFormat.format(
				"position ({0,number}, {1,number}) would be off the board!", x, y);
			throw new IllegalArgumentException(message);
		}
	}
	public String toString() {
		return (this.white?"White ":"Black ") + this.name + "(" + this.xPos +
				", " + this.yPos + ")";
	}
}

