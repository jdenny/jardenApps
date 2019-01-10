package demo;

import java.util.Date;
import java.util.GregorianCalendar;

public class Person {
	private String name;
	private Date dateOfBirth;
	
	public Person(String name, Date dob) {
		this.name = name;
		this.dateOfBirth = dob;
	}
	public String getName() {
		return name;
	}
	// get age now
	public int getAge() {
		return getYears(this.dateOfBirth, new Date());
	}
	
	public static void main(String[] args) {
		Person mike = new Person("Mike", makeDate(12, 3, 1980));
		int age = mike.getAge();
		String name = mike.getName();
		System.out.println(name + ": " + age + " years");
	}
	
	public static Date makeDate(int day, int month, int year) {
		return new GregorianCalendar(year, month, day).getTime();
	}
	public static int getYears(Date first, Date second) {
		return (int)((second.getTime() - first.getTime()) /
				(1000 * 3600 * 24 * 365.25));
	}
}
