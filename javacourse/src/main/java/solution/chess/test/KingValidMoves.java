package solution.chess.test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import solution.chess.King;

@RunWith(Parameterized.class)
public class KingValidMoves {
	private int startX;
	private int startY;
	private int endX;
	private int endY;
	
	@Parameters
	public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
        		{ 5, 1, 6, 1 },
        		{ 6, 1, 7, 2 },
        		{ 7, 2, 6, 3 },
        		{ 6, 3, 5, 2 },
        		{ 5, 2, 4, 1 }
        });
	}
	
	public KingValidMoves(int startX, int startY, int endX, int endY) {
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
	}

	@Test
	public void shouldBeValidMove() {
		King king = new King(true, startX, startY);
		assertEquals(this.startX, king.getXPos());
		assertEquals(this.startY, king.getYPos());
		king.move(endX, endY);
		assertEquals(this.endX, king.getXPos());
		assertEquals(this.endY, king.getYPos());
	}
}
