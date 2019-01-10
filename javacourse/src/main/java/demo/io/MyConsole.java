package demo.io;

import java.io.Console;

public class MyConsole {

	public static void main(String[] args) {
		Console console = System.console();
		if (console == null) {
			System.out.println("console not available!");
		} else {
			System.out.println("supply password: ");
			String password = new String(console.readPassword());
			if (password.equals("topsecret")) {
				System.out.println("passwork ok");
			} else {
				System.out.println("invalid password");
			}
		}
	}

}
