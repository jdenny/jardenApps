package solution;

import jarden.gui.ConsoleSwing;

public class FlowControl {
	public static void main(String[] args) {
		ConsoleSwing console = new ConsoleSwing();
		// while loop:
		console.println("even values from -6 to 10 using 'while' loop:");
		int i = -6;
		while (i <= 10) {
			console.println("\t" + i);
			i +=2;
		}

		// for loop:
		console.println("even values from -6 to 10 using 'for' loop:");
		for (int j = -6; j <= 10; j += 2) {
			console.println("\t" + j);
		}

		// find highest int:
		int a = console.getInt("supply integer a:");
		int b = console.getInt("supply integer b:");
		int c = console.getInt("supply integer c:");

		// version a:
		int max = a;
		if (b > max) {
			max = b;
		}
		if (c > max) {
			max = c;
		}
		console.println("highest is: " + max);
		// version b:
		if (a > b) {
			if (a > c) {
				console.println("highest is a: " + a);
			}
			else {
				console.println("highest is c: " + c);
			}
		}
		else {
			if (b > c) {
				console.println("highest is b: " + b);
			}
			else {
				console.println("highest is c: " + c);
			}
		}

		// print 3 numbers in descending order:
		max = a;
		int middle = b;
		int min = c;
		int temp = 0;
		if (middle > max) {
			temp = middle;
			middle = max;
			max = temp;
		}
		if (min > middle) {
			temp = min;
			min = middle;
			middle = temp;
			if (middle > max) {
				temp = middle;
				middle = max;
				max = temp;
			}
		}
		console.println("3 values in descending order are:\n\t" + max +
			"\n\t" + middle + "\n\t" + min);
	}
}
