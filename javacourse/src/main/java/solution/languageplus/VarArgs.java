package solution.languageplus;

/**
 * Sample solution to Exercise: More Language Features.
 */
public class VarArgs {

	public static void main(String[] args) {
		printSum("3 values", 1, 2, 3);
		printSum("1 value", 6);
		printSum("no values");
	}
	/**
	 * Print out title plus total from adding the int values.
	 * @param title
	 * @param values
	 */
	private static void printSum(String title, int... values) {
		int total = 0;
		for (int i: values) {
			total += i;
		}
		System.out.println(title + " " + total);
	}
	/**
	 * Method added to show interaction of method overloading
	 * with variable number of arguments.
	 * @param title
	 */
	private static void printSum(String title) {
		System.out.println("singleArg version called with title " +
				title);
	}
}

