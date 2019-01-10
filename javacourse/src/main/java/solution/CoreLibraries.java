package solution;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CoreLibraries {
	public static void main(String[] args) {
		Person john = new Person("Don Jenny", 4455, 123.4,
				new GregorianCalendar(1974, Calendar.APRIL, 8).getTime());
		System.out.println(john);
	}
}

class Person {
	private String name;
	private int friendCt;
	private double salary;
	/*private*/ Date dob;

	public Person(String name, int friendCt, double salary, Date dob) {
		this.name = name;
		this.friendCt = friendCt;
		this.salary = salary;
		this.dob = dob;
	}
	public String toString() {
		String formatStr =
			"name=%s, salary=£%01.2f, friends=%d, DOB=%tF";
		return String.format(formatStr,
			name, salary, friendCt, dob);
	}
}

