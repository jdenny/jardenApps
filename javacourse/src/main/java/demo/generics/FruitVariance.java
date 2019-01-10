package demo.generics;

import java.util.ArrayList;
import java.util.List;

abstract class Fruit { }
class Apple extends Fruit { }
class Banana extends Fruit { }
class Bramley extends Apple { }
class Coxs extends Apple { }

public class FruitVariance {

	public static void main(String[] args) {
		List<Apple> apples = new ArrayList<>();
		apples.add(new Apple());
		apples.add(new Bramley());
		List<Bramley> bramleys = new ArrayList<>();
		List<Fruit> fruits = new ArrayList<Fruit>();
		
		// bramley not superclass of apple; if allowed,
		// we could put coxs into bag of bramley:
		// updateFruitList(bramleys);
		updateAppleList(apples);
		updateAppleList(fruits); // can add apples to fruits
		
		consumeFruitList(bramleys);
		consumeFruitList(apples);
		consumeFruitList(fruits);
	}
	private static void updateAppleList(List<? super Apple> appleList) {
		// appleList.add(new Banana()); // okay for fruits, but not apples
		appleList.add(new Bramley());
		appleList.add(new Coxs());
		// could be list of fruit, or even objects:
		// for (Apple apple: appleList) System.out.println(apple);
		for (Object object: appleList) System.out.println(object);
	
	}
	private static void consumeFruitList(List<? extends Fruit> fruitList) {
		// fruitList.add(new Apple()); // could be list of bananas
		for (Fruit fruit: fruitList) System.out.println(fruit);
	}

}
