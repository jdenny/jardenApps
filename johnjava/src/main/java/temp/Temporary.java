package temp;

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
        }
    }
}


