package demo.jdk5;

public class Autoboxing {
	public static void main(String[] args) {
		// how things work from java5:
		Integer integerA = new Integer(5);
		
		int int5 = integerA; // pre-java5: type mismatch
		assert(int5 == 5);
		integerA = 55; // pre-java5: type mismatch
		assert(integerA == 55); // pre-java5: incompatible operand types
		System.out.println("int5=" + int5 + "; integerA=" + integerA);
		int5 = integerA + 5;
		// using wrapper object in expressions:
		for (Integer integerB = 0; integerB < 4; integerB++) {
			System.out.println("hello: " + integerB);
		}
	}
}
