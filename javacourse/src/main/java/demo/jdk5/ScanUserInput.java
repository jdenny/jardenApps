package demo.jdk5;

import java.util.Scanner;

public class ScanUserInput {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		String line;
		while (true) {
			System.out.println("type a line (blank to quit): ");
			line = scanner.nextLine();
			if (line.length() < 1) break;
			System.out.println("upper case line: " +
					line.toUpperCase());
		}
		scanner.close();
		System.out.println("Adios");
	}

}
