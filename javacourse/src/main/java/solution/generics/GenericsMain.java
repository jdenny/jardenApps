package solution.generics;

import java.util.ArrayList;
import java.util.Collections;

public class GenericsMain {

	public static void main(String[] args) {
		noGenerics();
		generics();
		System.out.println("wowabaweeba");
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void noGenerics() {
		ArrayList intList = new ArrayList();
		intList.add(24);
		intList.add(new Integer(17));
		intList.add(19);
		intList.add(-23);
		System.out.println("unsorted integer list:");
		for (Object element: intList) {
			System.out.println(element);
		}
		Collections.sort(intList);
		System.out.println("sorted integer list:");
		for (Object element: intList) {
			System.out.println(element);
		}
	}

	private static void generics() {
		ArrayList<Integer> intList = new ArrayList<>();
		intList.add(24);
		intList.add(new Integer(17));
		intList.add(19);
		intList.add(-23);
		System.out.println("unsorted integer list:");
		for (Object element: intList) {
			System.out.println(element);
		}
		Collections.sort(intList);
		System.out.println("sorted integer list:");
		for (Object element: intList) {
			System.out.println(element);
		}
	}
	
}
