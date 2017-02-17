package jarden.maze;

import java.awt.Color;

public class Player {
	// co-ordinates of player, as a maze coordinate:
	int x, y, oldX, oldY;
	Color colour;
	boolean gotKey; // can open a gate

	public Player(Color colour) {
		this.colour = colour;
	}
}
