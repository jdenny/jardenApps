package solution.chess.test;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import solution.chess.King;

@RunWith(Parameterized.class)
public class KingInvalidMoves {
	private int startX;
	private int startY;
	private int endX;
	private int endY;
	
	@Parameters
	public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
        		{ -1, 2, 6, 1 }, // invalid starts
        		{ 1, 0, 7, 2 },
        		{ 1, 9, 6, 3 },
        		{ 9, 2, 5, 2 },
        		{ 5, 1, 5, 0 }, // invalid moves: off board
        		{ 4, 8, 4, 9 },
        		{ 1, 2, 0, 2 },
        		{ 8, 2, 9, 2 },
        		{ 8, 8, 9, 9 },
        		{ 1, 2, 3, 2 }, // invalid moves for king
        		{ 2, 3, 2, 3 },
        		{ 3, 4, 1, 4 },
        		{ 4, 5, 4, 3 },
        		{ 4, 5, 4, 7 },
        });
	}
	
	public KingInvalidMoves(int startX, int startY, int endX, int endY) {
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldBeInvalidMove() {
		King king = new King(true, startX, startY);
		king.move(endX, endY);
	}
}
