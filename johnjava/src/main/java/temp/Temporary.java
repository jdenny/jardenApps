package temp;

/**
 * Created by john.denny@gmail.com on 06/03/2026.
 */
public class Temporary {
    public static void main(String[] args) {
        Temporary temp = new Temporary();
        temp.doSomething(new DoIt(), "Hello");
        temp.doSomething(new DoIt2(), "Still here");
        temp.doSomething(new PrintIt() {
            @Override
            public void printMessage(String message) {
                System.out.println("Anonymous; Message=" + message);
            }
        }, "GoodBye");
    }
    private void doSomething(PrintIt printIt, String message) {
        printIt.printMessage(message);
    }

    private interface PrintIt {
        void printMessage(String message);
    }
    private static class DoIt implements PrintIt {
        public void printMessage(String message) {
            System.out.println("DoIt; Message=" + message);
        }
    }
    private static class DoIt2 implements PrintIt {
        public void printMessage(String message) {
            System.out.println("DoIt2; Message=" + message);
        }
    }
}
