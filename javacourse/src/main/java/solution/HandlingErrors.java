package solution;

import jarden.gui.ConsoleSwing;

public class HandlingErrors {
	public static void main(String[] args) {
		ConsoleSwing console = new ConsoleSwing();
		// hard-coded codes
		char [] directions = {'N', 'S', 'X', 'E', 'N', 'W', 'S'};
		for (int i = 0; i < directions.length; i++) {
			try {
				console.println(getDirection(directions[i]));
			} catch (NoDirectionHomeException e) {
				console.println("caught exception: " + e);
			}
		}
		
		// assert that exception is thrown for invalid direction;
		// don't forget to enable assertions:
		// pass vm argument of -ea or -enableassertions
		try {
			// getDirection('N');
			// assert false: "no exception thrown for direction N";
			getDirection('Z');
			assert false: "no exception thrown for direction Z";
		} catch (NoDirectionHomeException e1) {
			console.println("exception correctly thrown for direction Z");
		}

		// codes supplied interactively:
		String prompt = "supply a compass direction (N, S, E, W) or q to quit: ";
		while (true) {
			String inputLine = console.getString(prompt);
			if (inputLine.equalsIgnoreCase("q")) break;
			try {
				console.println(getDirection(inputLine.charAt(0)));
			} catch (NoDirectionHomeException e) {
				console.println("caught exception: " + e);
			}
		}
		System.out.println("viaje seguro");
		console.close();
	}

	// method for parts 2 & 3:
	public static String getDirection(char c) throws NoDirectionHomeException {
		String direction;
		switch (c) {
		case 'N':
		case 'n':
			direction = "North";
			break;
		case 'S':
		case 's':
			direction = "South";
			break;
		case 'E':
		case 'e':
			direction = "East";
			break;
		case 'W':
		case 'w':
			direction = "West";
			break;
		default:
			throw new NoDirectionHomeException(c
					+ " not recognised as compass direction");
		}
		return direction;
	}
}
