package demo.language;

interface Greeting {
	void sayHello(String name);
}

/** 
 * Demonstration to show you can create inner classes within:
 * 	another class, a method, a method call.
 * @author John
 *
 */
public class InnerClasses {
	
	public static void main(String[] args) {
		new InnerClasses();
		System.out.println("adios");
	}
	
	// class defined within another class:
	class GreetInClass implements Greeting {
		@Override
		public void sayHello(String name) {
			System.out.println("hello " + name);
		}
	}

	static class StaticGreetInClass implements Greeting {
		@Override
		public void sayHello(String name) {
			System.out.println("bonjour " + name);
		}
	}
	public InnerClasses() {
		// class defined within a method:
		class GreetInMethod implements Greeting {
			@Override
			public void sayHello(String name) {
				System.out.println("hola " + name);
			}
		}
		myMethod(new GreetInClass(), "Sarai");
		myMethod(new StaticGreetInClass(), "Joe");
		myMethod(new GreetInMethod(), "Julie");
		myMethod(new Greeting() {
			@Override
			public void sayHello(String name) {
				System.out.println("hallo " + name);
			}
		}, "Sam");
	}
	public void myMethod(Greeting greeting, String name) {
		System.out.println("greeting class is " + greeting.getClass());
		greeting.sayHello(name);
	}
}

class TestVisibility {
	public void test() {
		InnerClasses outer = new InnerClasses();
		// next line error if GreetInClass is private
		InnerClasses.GreetInClass inner = outer.new GreetInClass();
		inner.sayHello("Angela");
		InnerClasses.StaticGreetInClass innerStatic = new InnerClasses.StaticGreetInClass();
		innerStatic.sayHello("Jackie");
		// outer.new GreetInMethod(); // compile error
	}
}
