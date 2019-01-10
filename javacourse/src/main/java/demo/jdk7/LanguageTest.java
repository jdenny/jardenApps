package demo.jdk7;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class LanguageTest {

	@Test
	public void binaryLiterals() {
		// also shows use of underscores within numeric literals
		// to make the literal more readable
		int bitInt = 0b11111111_11111111_11111111_11111110;
		assertEquals(-2, bitInt);
		bitInt = 0b10101;
		assertEquals(21, bitInt);
		for (int i = 0; i < 10; i++) {
			System.out.println("bitInt=" + bitInt);
			System.out.println("shifting bitInt 4 places to the left");
			bitInt <<= 4;
		}
		assertEquals(0, bitInt);
		long creditCardNumber = 1234_5678_9012_3456L;
		assertEquals(creditCardNumber, 1234567890123456L);
	}
	private String getSpanishColour(String colour) {
		String spanColour;
		switch (colour) {
		case "red":
			spanColour = "rojo";
			break;
		case "orange":
			spanColour = "naranja";
			break;
		case "yellow":
			spanColour = "amarillo";
			break;
		case "green":
			spanColour = "verde";
			break;
		case "blue":
			spanColour = "azul";
			break;
		default:
			spanColour = "unknown";
		}
		return spanColour;
	}
	@Test
	public void getSpanishColours() {
		assertEquals("verde", getSpanishColour("green"));
		assertEquals("rojo", getSpanishColour("red"));
		assertEquals("amarillo", getSpanishColour("yellow"));
		assertEquals("unknown", getSpanishColour("horrible browny colour"));
	}
	@Test
	public void tryWithResourceTest() {
		SimpleResource sr = new SimpleResource();
		try {
			sr.divide(10, 2);
			System.out.println("isBusy() after good divide: " + sr.isBusy());
			sr.divide(10, 0);
			fail("Exception not thrown; isBusy() after bad divide: " + sr.isBusy());
		} catch(Exception e) {
			System.out.println("caught exception: " + e);
		} finally {
			try {
				sr.close();
				System.out.println("isBusy() after close: " + sr.isBusy());
			} catch(Exception e) {
				System.out.println("caught exception after close: " + e);
			}
		}
		try (SimpleResource sr2 = new SimpleResource()) {
			sr2.divide(10, 2);
			System.out.println("sr2.isBusy() after good divide: " + sr2.isBusy());
			sr2.divide(10, 0);
			fail("Exception not thrown; isBusy() after bad divide: " + sr2.isBusy());
		} catch(Exception e) {
			System.out.println("caught exception on sr2: " + e);
		}
	}
	private void multiException(int action)
			throws NumberFormatException, IOException, IllegalArgumentException {
		try {
			switch(action) {
			case 1:
				String s = "this is not an integer";
				Object o = s;
				Integer integer = (Integer)o; // should throw ClassCastException
				fail("ClassCastException not thrown; integer=" + integer);
				break;
			case 2:
				Integer.parseInt("should throw NumberFormatException");
				fail("NumberFormatException not thrown");
				break;
			case 3:
				FileInputStream fis = new FileInputStream("should throw FileNotFoundException");
				fis.close();
				fail("FileNotFoundException not thrown");
				break;
			default:
				throw new IllegalArgumentException("unrecognised action: " + action);
			}
			
		} catch(ClassCastException | IOException | IllegalArgumentException ex) {
			System.out.println("exception caught: " + ex);
			throw ex;
		}
	}
	/**
	 * The compiler is clever enough to recognise that although
	 * the "throw ex;" statement below is declared to throw "Exception",
	 * in reality that exception is either InterruptedException or
	 * IOException, so we can declare these 2 types in the
	 * "throws" clause.
	 * @throws InterruptedException
	 * @throws java.io.IOException
	 */
	public void rethrowException()
			throws InterruptedException, java.io.IOException {
		try {
			Thread.sleep(2000);
			new java.io.FileInputStream("hello.txt");
			// the following statement would cause a compilation error
			// on "throw ex;" below, unless we added SQLException as a
			// "throws" type.
			// throw new java.sql.SQLException();
		} catch (Exception ex) {
			System.out.println("hello");
			throw ex; // would be compile error in JDK6
		}
	}
	@Test
	public void multiCatchTest() {
		for (int i = 1; i <= 3; i++) {
			try {
				multiException(i);
			} catch (Exception e) {
				System.out.println("exception returned to test: " + e);
			}
		}
	}
}

class SimpleResource implements AutoCloseable {
	private boolean busy = false;
	
	public SimpleResource() {
		this.busy = true;
	}
	public boolean isBusy() {
		return busy;
	}
	public int divide(int a, int b) {
		return a / b;
	}

	@Override
	public void close() throws Exception {
		System.out.println("SimpleResource.close() called");
		if (!busy) {
			throw new IllegalStateException("simple resource not open");
		}
		busy = false;
	}
	
}

