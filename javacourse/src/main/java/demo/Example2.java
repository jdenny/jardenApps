package demo;

public class Example2 {
	public static void notReally() {
		int aa = 123;
		System.out.println("aa = " + aa); // prints 123
		// System.out.println("bb = " + bb); // compiler error
	}
	public static void main(String[] args) {
		for (int i = 0; i < args.length; i++) {
			System.out.println("args[" + i + "]=" + args[i]);
		}
		// declare and use variables
		int partNumber;
		String description;
		double cost;
		boolean inHouse = true;
		int soldToday = 0;
		double valueToday = 0;
		partNumber = 1234;
		description = "2-inch number 8 wood screws - box of 100";
		cost = 2.5;
		soldToday = soldToday + 5; // soldToday is 5
		valueToday = soldToday * cost; // valueToday is 12.5

		// shorthand operators
		soldToday += 5; // same as soldToday = soldToday + 5

		cost *= 1.25; // same as cost = cost * 1.25;

		soldToday++; // same as soldToday = soldToday + 1;
		soldToday--; // same as soldToday = soldToday - 1;
		int x, y = 10, z; // declare 3 int variables

		// set x to old value of y, 10, then set y to 11:
		x = y++;
		// set y to 12, then set z to new value of y, 12:
		z = ++y;

		// String operators:
		String name, forename, surname;
		forename = "Bobby";
		surname = "Charlton";
		// set name to "Bobby Charlton":
		name = forename + " " + surname;
		int count = 5;
		String message;
		// set message to "the value of count is 5":
		message = "the value of count is " + count;

		int order = 300;
		int stock = 15;
		boolean outOfStock, stockLow, reorder;
		int sale, cash=95;
		
		// Comparisons:
		outOfStock = (stock == 0); // outOfStock is false
		stockLow = (stock <= 20); // stockLow is true
		reorder = stockLow && (cash > 100); // reorder is false

		//***********************Control Statements******************
		// 'if' statement
		if (x > y)
			System.out.println("x is larger than y");

		if (x > y)
			System.out.println("x is larger than y");
		else
			System.out.println("x is not larger than y");

		if (order <= stock) {
			System.out.println("Order met");
			sale = order;
		} else {
			System.out.println("Insufficient stock!");
			sale = stock;
		}
		stock -= sale;

		// 'if' common errors
		// if (a = b) // assignment instead of equality operator

		if (order <= stock)
			;
		{ // semicolon terminates if statement
			// these statements always executed, as they
			// are not part of the if statement:
			System.out.println("Order met");
			sale = order;
		}

		if (order <= stock) // note lack of braces!
			System.out.println("Order met"); // executed if true
		sale = order; // always executed: not part of if

		// nested 'if'
		if (outOfStock) {
			System.out.println("No stock left!");
		} else {
			if (order <= stock) {
				System.out.println("Order met");
				sale = order;
			} else {
				System.out.println("Insufficient stock!");
				sale = stock;
			}
			stock -= sale;
		}

		// multiple 'if's on same variable...
		char oper = '*';
		int arg1 = 5;
		int arg2 = 3;
		int result = 0;
		if (args.length == 3) {
			oper = args[1].charAt(0);
			arg1 = Integer.parseInt(args[0]);
			arg2 = Integer.parseInt(args[2]);
		}

		if (oper == '+')
			result = arg1 + arg2;
		else if (oper == '-')
			result = arg1 - arg2;
		else if (oper == '*')
			result = arg1 * arg2;
		else if (oper == '/')
			result = arg1 / arg2;
		else {
			System.out.println("invalid operator: " + oper);
			System.exit(1); // abort application
		}
		System.out.println("result = " + result);

		// can be replaced by 'switch':
		switch (oper) {
		case '+':
			result = arg1 + arg2;
			break; // go to statement after switch
		case '-':
			result = arg1 - arg2;
			break;
		case '*':
			result = arg1 * arg2;
			break;
		case '/':
			result = arg1 / arg2;
			break;
		default:
			System.out.println("invalid operator: " + oper);
			System.exit(1); // abort application
		}
		System.out.println("result = " + result);

		// 'while' statement:
		System.out.println("Three times table:");
		int j = 1;
		while (j < 13) {
			System.out.println("  " + j + " times 3 = " + j * 3);
			j++;
		}
		System.out.println("End of table");
		
		// 'do' statement:
		int total = 0;
		do {
			total += 10;
		} while (total < 100);
		
		// while mistake:
		while (total < 100); // will loop indefinitely
			total += 10;

		// 'for' statement:
		System.out.println("Three times table:");
		for (int i = 1; i < 13; i++) {
			System.out.println("  " + i + " times 3 = " + i * 3);
		}
		System.out.println("End of table");
		
		for (;;) {
			if (args.length >= 0) break;
			
		}
		while (true) {
			if (args.length >= 0) break;
		}

		// arrays
		int maxNumber = 10;
		int month = 3;

		double[] salesFigures;
		salesFigures = new double[12];
		salesFigures[6] = 1234.56;
		salesFigures[month] += 6.54;

		String[] messages2;
		messages2 = new String[maxNumber];
		messages2[2] = "No stock left";

		int[] dailySales;
		dailySales = new int[5];
		dailySales[2] = 37;

		// array shortcuts
		String[] messages;
		messages = new String[4];
		messages[0] = "stock low";
		messages[1] = "stock out";
		messages[2] = "awaiting delivery";
		messages[3] = "order complete";

		String[] messages3 = { "stock low", "stock out", "awaiting delivery",
				"order complete" };

		// process all elements of an array
		double salesTotal = 0.0;
		for (int i = 0; i < salesFigures.length; i++) {
			salesTotal += salesFigures[i];
		}
		System.out.println("salesTotal: " + salesTotal);
		// or using java 5 for each:
		salesTotal = 0;
		for (double figure: salesFigures) {
			salesTotal += figure;
		}
		System.out.println("salesTotal: " + salesTotal);
		displayMenu(); // invoke the method
		int a = 5, b = 12;
		int diff = difference(a, b);
		System.out.println("diff = " + diff);
		a = 20;
		System.out.println("diff = " + difference(a, b));

		// variable scope
		int aa = 5, bb = 12;
		notReally();

		for (int i = 0; i < salesFigures.length; i++) {
			double sf = salesFigures[i];
			salesTotal += sf;
		}
		String sf = "hello";

	}

	// define the method:
	public static void displayMenu() {
		System.out.println("Order Entry System");
		System.out.println("1 - create new order");
		System.out.println("2 - re-order stock");
		System.out.println("3 - view sales figures");
		System.out.println("4 - exit from system");
	}

	public static int difference(int arg1, int arg2) {
		if (arg1 > arg2)
			return (arg1 - arg2);
		else
			return (arg2 - arg1);
		// or return Math.abs(arg1 - arg2);
	}

	/**
	 * Some javadoc
	 */
	public static void declareVariables() {
		boolean inHouse; // valid 
		char arg2; // valid
		// char 2arg; // invalid: must start with letter
		// boolean in House; // invalid: can't contain spaces
		char _arg; // valid but unconventional
		boolean $in_house; // valid but unconventional

	}
}

class Orders {
	private static int salesTotal;
	private static int[] salesFigures;

	public static void main(String[] args) {
		salesFigures = new int[12];
		// ... // set values
		printFigures();
	}

	public static void printFigures() {
		System.out.println("month 5 = " + salesFigures[4]);
		// ...
		System.out.println("salesTotal = " + salesTotal);
	}
}