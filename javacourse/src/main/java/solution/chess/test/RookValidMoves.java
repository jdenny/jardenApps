package solution.chess.test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import solution.chess.Rook;

@RunWith(Parameterized.class)
public class RookValidMoves {
	private Rook rook;
	private int startX;
	private int startY;
	private int endX;
	private int endY;
	
	@Parameters
	public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
        		{ 8, 1, 1, 1 },
        		{ 1, 8, 8, 8 },
        		{ 7, 2, 8, 2 },
        		{ 6, 3, 6, 1 },
        		{ 5, 2, 5, 8 }
        });
	}
	
	public RookValidMoves(int startX, int startY, int endX, int endY) {
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		rook = new Rook(true, startX, startY);
	}

	@Test
	public void shouldBeValidMove() {
		assertEquals(this.startX, rook.getXPos());
		assertEquals(this.startY, rook.getYPos());
		rook.move(endX, endY);
		assertEquals(this.endX, rook.getXPos());
		assertEquals(this.endY, rook.getYPos());
	}
}
