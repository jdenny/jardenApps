package demo;

public class PersonBadDesign {
	String name;
	int age;

	public static void main(String[] args) {
		PersonBadDesign mike = new PersonBadDesign();
		mike.name = "Mike";
		mike.age = 32;
		
		int mikeAge = mike.age;
		System.out.println("mike is now " + mikeAge + " years old");
	}

}
