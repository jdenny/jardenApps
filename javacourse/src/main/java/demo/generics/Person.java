package demo.generics;

public class Person implements Comparable<Person> {
	private String name;
	private double salary;
	private int id;

	public Person(String name, double salary, int id) {
		super();
		this.name = name;
		this.salary = salary;
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getSalary() {
		return salary;
	}
	public void setSalary(double salary) {
		this.salary = salary;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	@Override
	public int hashCode() {
		// return name.hashCode();
		return id;
	}
	@Override
	public boolean equals(Object obj) {
		// return (obj instanceof Person) && this.name.equals(((Person)obj).getName());
		return (obj instanceof Person) && this.id == ((Person)obj).getId();
	}
	@Override
	public int compareTo(Person anotherPerson) {
		return this.name.compareTo(anotherPerson.name);
	}
	@Override
	public String toString() {
		return name + ":" + salary + ":" + id;
	}
}
