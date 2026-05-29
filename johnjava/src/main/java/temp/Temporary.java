package temp;

import java.util.Scanner;

/**
 * Created by john.denny@gmail.com on 06/03/2026.
 */
public class Temporary {
    private String name = "hello John";
    public static void main(String[] args) {
        new Temporary();
    }
    public Temporary() {
        new TestClass();
    }
    private class TestClass {
        public TestClass() {
            System.out.println(name);
            Scanner scanner = new Scanner(System.in);
            for (int i = 0; i < 3; i++) {
                System.out.println("supply a number:");
                if (scanner.hasNextLine()) {
                    String text = scanner.nextLine();
                    System.out.println("you supplied: " + text);
                    try {
                        int number = Integer.parseInt(text);
                        System.out.println("you supplied: " + number);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number format: " + text);
                    }
                }
            }
        }
    }
}
