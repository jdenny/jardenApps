package demo.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestSums {
	private int a, b;

	@Before
	public void setUp() {
		Random random = new Random();
		a = random.nextInt(20);
		b = random.nextInt(20);
	}

	@Test
	public void shouldAdd() {
		assertEquals(Sums.add(a, b), (a + b));
	}

	@Test
	public void shouldSubtract() {
		assertEquals(Sums.subtract(a, b), (a - b));
	}

	@Test
	public void shouldDivide() {
		if (b==0) ++b;
		assertEquals(Sums.divide(a, b), (a / b));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentException() {
		Sums.divide(a, 0);
		fail("zero divide didn't throw exception");
	}

	@Test
	public void shouldMultiply() {
		assertEquals(Sums.multiply(a, b), (a * b));
	}
	@After
	public void cleanUp() {
		System.out.println("cleanup called");
	}

}
