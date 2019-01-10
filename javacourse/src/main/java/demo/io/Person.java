package demo.io;

import java.io.Serializable;

public class Person implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private boolean male;
	private int salary;

	public void setName(String name) {
		this.name = name;
	}
	public void setMale(boolean male) {
		this.male = male;
	}
	public void setSalary(int salary) {
		this.salary = salary;
	}
	public Person(String name, boolean male, int salary) {
		this.name = name;
		this.male = male;
		this.salary = salary;
	}
	public Person() {
		
	}
	public String toString() {
		return name + "; " + (male?"male":"female") + "; salary=" + salary; 
	}
	public String getName() {
		return name;
	}
	public boolean isMale() {
		return male;
	}
	public int getSalary() {
		return salary;
	}
}

