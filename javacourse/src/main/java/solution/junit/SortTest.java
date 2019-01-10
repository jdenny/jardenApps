package solution.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class SortTest {
	private int[] numbers;
	
	@Before
	public void setUp() {
		numbers = new int[] {4, 25, 16, -3, 42, 4};
		
	}

	@Test
	public void testSort() {
		MySort.sort(numbers);
		assertEquals(-3, numbers[0]);
		assertEquals(4, numbers[1]);
		assertEquals(4, numbers[2]);
		for (int i = 0; i < numbers.length - 1; i++) {
			assertTrue(numbers[i] <= numbers[i+1]);
		}
	}

	@Test
	public void testReverseSort() {
		MySort.reverseSort(numbers);
		assertEquals(42, numbers[0]);
		assertEquals(25, numbers[1]);
		assertEquals(16, numbers[2]);
		for (int i = 0; i < numbers.length - 1; i++) {
			assertTrue(numbers[i] >= numbers[i+1]);
		}
	}

}
