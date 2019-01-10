package jarden.gui;

public interface Chess {
	/**
	 * Return x position, where x = 1 is left-most column of board for white
	 * and x = 8 is right-most column for white.
	 */
	public int getXPos();
	
	/**
	 * Return y position, where y = 1 is row of board nearest white
	 * and 1 = 8 is row nearest black.
	 */
	public int getYPos();
	
	public String getName();
	public String getShortName();
	
	/**
	 * Move piece to new absolute positions.
	 * Ensure new position is on the board, and call
	 * validateMove(x, y).
	 * @param x new x position
	 * @param y new y position
	 */
	public void move(int x, int y);
	
	/**
	 * Ensure that the move is valid for this type of piece,
	 * e.g. a king can move 1 square in any direction.
	 * @param x new x position
	 * @param y new y position
	 */
	public void validateMove(int x, int y);

}
