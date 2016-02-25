package jarden.maze;

public interface MazeListener {
	/**
	 * Increment the game level, and return the new value.
	 * Called on achieving some goal, e.g. reached end of maze.
	 * @return new game level
	 */
	int onNextLevel();
	void onLost();
	void onLookOut();
	int getLevel();
}
