package solution.chess.test;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import solution.chess.Rook;

@RunWith(Parameterized.class)
public class RookInvalidMoves {
	private int startX;
	private int startY;
	private int endX;
	private int endY;
	
	@Parameters
	public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
        		{ 0, 0, 6, 1 }, // invalid starts
        		{ 1, 10, 7, 2 },
        		{ 1, -3, 6, 3 },
        		{ 9, 9, 5, 2 },
        		{ 1, 1, 1, 0 }, // invalid moves: off board
        		{ 5, 8, 5, 9 },
        		{ 1, 2, 0, 2 },
        		{ 8, 2, 9, 2 },
        		{ 1, 2, 2, 3 }, // invalid moves for rook
        		{ 3, 2, 3, 2 },
        		{ 3, 4, 5, 3 },
        		{ 4, 5, 5, 4 },
        		{ 3, 5, 4, 7 },
        });
	}
	
	public RookInvalidMoves(int startX, int startY, int endX, int endY) {
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldBeInvalidMove() {
		Rook rook = new Rook(true, startX, startY);
		rook.move(endX, endY);
	}
}
