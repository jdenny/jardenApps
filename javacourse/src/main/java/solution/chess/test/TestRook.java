package solution.chess.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import solution.chess.Rook;

public class TestRook {

	@Test
	public void shouldValidateMove() {
		Rook rook = new Rook(false, 1, 8);
		assertEquals(1, rook.getXPos());
		assertEquals(8, rook.getYPos());
		rook.move(2, 8);
		rook.move(2, 6);
		rook.move(4, 6);
		rook.move(4, 3);
		assertEquals(4, rook.getXPos());
		assertEquals(3, rook.getYPos());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldDetectIllegalMove() {
		Rook rook = new Rook(false, 1, 8);
		rook.move(2, 7);
	}

}
