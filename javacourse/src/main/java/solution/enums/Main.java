package solution.enums;

import jarden.gui.ConsoleSwing;

public class Main {

	public static void main(String[] args) {
		ConsoleSwing console = new ConsoleSwing();
		for (DayOfWeek day: DayOfWeek.values()) {
			console.println("if today is " + day +
					" then tomorrow is " + day.getNext());
		}
		for (Country country: Country.values()) {
			console.println(country.getName() + ", " +
					country.getCapital());
		}
		for (Country country: Country.values()) {
			String line = console.getString("What is the capital of " +
					country.getName() + "? ");
			if (country.isCapital(line)) {
				console.println("correct!");
			} else {
				console.println("wrong! the capital is " + country.getCapital());
			}
		}
		System.out.println("Dosvidaniya!");
	}
}
