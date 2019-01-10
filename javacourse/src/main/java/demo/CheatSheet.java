// all names in java to consist of letters and numbers, start with a letter;
// special cases can include $ and _
// strongly recommend names start with lower case, except class names which should
// start with upper case
// package names by convention all lower case
// can have no package (known as default package), but not recommended
package demo;

/**
 * Define a class. Strongly recommend name starts with capital letter.
 * Each source file can contain at most one public class.
 * 'public' means visible to all.
 */
class Employee {
	// declare instance variables (every object will have its own copy)
	// as rule of thumb, instance variables are private
	// need to declare type and name
	private String firstName;
	private double salary;
	// optionally can initialise in declaration
	private int number = nextNumber++;
	private static int nextNumber = 200;
	
	// define a constructor; name is same as class-name
	// if no constructor, compiler supplies default one, with no parameters
	// don't declare a return type ('new' returns object reference)
	public Employee(String firstName, double salary) {
		this.firstName = firstName;
		this.salary = salary;
	}
	// define a method: visibility returnType name(argType argValue, type2 value2...)
	// visibility: mostly public, unless only used within this class, then private
	// zero or more arguments; always use parenthesis to wrap arguments. Code is
	// defined within { and }
	public void awardPayRise(int percentage) {
		salary = salary * (100 + percentage) / 100;
	}
	// override the toString method inherited from class Object; this method used
	// by:   System.out.println(Object obj);
	// and:  String str; Object obj; String message = str + obj;
	@Override
	public String toString() {
		return firstName + ", salary=" + salary +
				", number=" + number;
	}
}

public class CheatSheet {
	// entry point for JVM; e.g. java demo.TestCheatSheet
	// signature must be as follows:
	public static void main(String[] args) {
		// old for loop:
		for (int i = 0; i < args.length; i++) {
			System.out.println(args[i]);
		}
		// new for loop:
		for (String arg: args) {
			System.out.println(arg);
		}
		// create an object of type Employee; also calls constructor
		Employee emp = new Employee("Juan", 720.50);
		// invoke a method on this object:
		emp.awardPayRise(3);
	}
}
