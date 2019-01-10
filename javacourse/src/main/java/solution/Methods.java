package solution;

import jarden.gui.ConsoleSwing;

public class Methods {
	public static void main(String[] args) {
		// part 1:
		int [] numbers1 = {1, 3, 7, 15, 31};
		System.out.println("sum=" + sum(numbers1));
		int [] numbers2 = {4, 25, 36, 49, 9, 1};
		System.out.println("sum=" + sum(numbers2));

		// part 2:
		char [] directions = {'N', 'S', 'E', 'N', 'W', 'S'};
		for (int i = 0; i < directions.length; i++) {
			System.out.println(directions[i] + ": " + getDirection(directions[i]));
		}

		// part 3:
		ConsoleSwing console = new ConsoleSwing();
		String inputLine = console.getString(
				"supply string of directions (N, S, E, W): ");
		for (int i = 0; i < inputLine.length(); i++) {
			String direction = getDirection(inputLine.charAt(i));
			console.println(direction);
		}
	}

	// method for part 1:
	public static int sum(int [] numbers) {
		int sum = 0;
		int max = numbers[0];
		for (int i = 0; i < numbers.length; i++) {
			sum += numbers[i];
			if (numbers[i] > max) {
				max = numbers[i];
			}
		}
		System.out.println("highest value: " + max);
		return sum;
	}

	// method for parts 2 & 3:
	public static String getDirection(char c) {
		String direction;
		switch (c) {
		case 'N':
			direction = "North";
			break;
		case 'S':
			direction = "South";
			break;
		case 'E':
			direction = "East";
			break;
		case 'W':
			direction = "West";
			break;
		default:
			direction = "Unknown direction code: " + c;
		}
		return direction;
	}
}
