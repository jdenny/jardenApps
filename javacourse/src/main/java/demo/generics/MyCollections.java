package demo.generics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MyCollections {
	private final static String[] dias = {
		"sábado", "lunes", "martes", "miércoles", "jueves",
		"viernes", "domingo"
	};

	public static void main(String[] args) {
		demoStringEquality();
		demoArrays();
		demoCollections();
		demoIterator();
	}
	private static void demoStringEquality() {
		System.out.println("demoStringEquality()*************");
		String name = "Nebuchad";
		name += "nezzar";
		String name2 = "Nebuchadnezzar";
		System.out.println("name == name2: " + (name == name2));
		System.out.println("name.equals(name2): " + name.equals(name2));
	}
	private static void demoArrays() {
		System.out.println("demoArrays()*************");
		String[] stringArray = {"one", "two", "three"};
		Object[] objectArray = {"four", new Double(5.6), new MyCollections()};
		String[] strArray;
		Object[] objArray;
		objArray = stringArray; // compiler allows, and maybe okay...
		for (Object obj: objArray) {
			System.out.println(obj);
		}
		// but could be dodgy...
		objArray[1] = "four"; // okay
		try {
			objArray[0] = new Double(4.5);  // compiles okay, but runtime exception
		} catch (ArrayStoreException ase) {
			System.out.println(ase);
		}
		try {
			strArray = (String[]) objectArray; // compiler allows with cast, causes exception
			strArray[0].toUpperCase(); // we never reach this
		} catch(ClassCastException cce) {
			System.out.println(cce);
		}
	}
	private static void demoCollections() {
		System.out.println("demoCollections()*************");
		List<String> stringList = new ArrayList<>();
		stringList.add("uno");
		stringList.add("dos");
		stringList.add("tres");
		System.out.println(stringList);
		Collections.sort(stringList);
		System.out.println(stringList); // sorted!
		
		List<Person> personList = new ArrayList<>();
		personList.add(new Person("John", 12.32, 101));
		personList.add(new Person("Julie", 120.7, 102));
		personList.add(new Person("Jackie", 18.5, 103));
		personList.add(new Person("Joe", 85.3, 104));
		personList.add(new Person("Joe", 87.6, 104)); // deliberate duplication by name
		personList.add(new Person("Dad", 12.32, 101));
		personList.add(new Person("Mum", 120.7, 102));
		System.out.println("personList (order added to list):\n   " + personList);
		Collections.sort(personList);
		System.out.println("sorted by name (default for Person):\n   " + personList);
		Collections.sort(personList, new Comparator<Person>() {
			@Override
			public int compare(Person o1, Person o2) {
				return Double.compare(o1.getSalary(), o2.getSalary());
			}
		});
		System.out.println("sorted by salary:\n   " + personList);
		Collections.sort(personList, new ComparatorId());
		System.out.println("sorted by Id:\n   " + personList);
		
		Set<Person> personSet = new HashSet<Person>(personList);
		System.out.println(
				"personSet (Person equality based on Id):\n   :" + personSet);
		HashMap<String, Person> personMap = new HashMap<>();
		for (Person person: personList) {
			personMap.put(person.getName(), person);
		}
		// ad hoc retrieval:
		System.out.println(personMap.get("Joe"));
	}
	private static void demoIterator() {
		System.out.println("demoIterator()*************");
		List<String> dayList = new ArrayList<>(Arrays.asList(dias));
		System.out.println("forLoop of dayList");
		for (String dia: dayList) {
			if (dia.startsWith("j")) {
				dayList.remove(dia);
				System.out.println("  removed: " + dia);
				break; // otherwise would get ConcurrentModificationException
			}
			System.out.println("  " + dia);
		}
		printDays(dayList);
		Iterator<String> iterator = dayList.iterator();
		System.out.println("iterator navigation of dayList");
		while (iterator.hasNext()) {
			String name = iterator.next();
			if (name.startsWith("m")) {
				iterator.remove();
				System.out.println("  removed: " + name);
			} else {
				System.out.println("  " + name);
			}
		}
		printDays(dayList);
	}
	private static void printDays(List<String> days) {
		System.out.println("printDays:");
		for (String day: days) {
			System.out.println("  " + day);
		}
	}
}

class ComparatorId implements Comparator<Person> {
	@Override
	public int compare(Person p1, Person p2) {
		return p1.getId() - p2.getId();
		/* long-hand:
		if (p1.getId() < p2.getId()) return -1;
		else if (p1.getId() > p2.getId()) return 1;
		else return 0;
		*/
	}
}

