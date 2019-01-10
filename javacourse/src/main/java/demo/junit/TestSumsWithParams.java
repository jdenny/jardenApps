package demo.junit;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TestSumsWithParams {
	private int a, b;
	private int result;

	@Parameters
	public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
        		{  5, 2, 7 },
        		{ -5, 2, -3},
        		{  0, 2, 2 },
        		{ -5, 0, -5}
        });
	}
	public TestSumsWithParams(int a, int b, int res) {
		this.a = a;
		this.b = b;
		this.result = res;
	}
	@Test
	public void shouldAdd() {
		assertEquals(Sums.add(a, b), result);
		// next line for debug only:
		System.out.println("a=" + a + ", b=" + b + ", res=" + result);
	}
}
