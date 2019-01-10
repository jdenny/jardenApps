package solution.generics;

import java.util.ArrayList;
import java.util.Random;

public class RandomList<E> extends ArrayList<E> {
	private static final long serialVersionUID = 1L;

	public E getRandomElement() {
		Random randomNum = new Random();
		int r = randomNum.nextInt(super.size());
		return super.get(r);
	}
	
	public static void main(String[] args) {
		RandomList<String> randomStrings = new RandomList<>();
		randomStrings.add("Hearts");
		randomStrings.add("Clubs");
		randomStrings.add("Diamonds");
		randomStrings.add("Spades");
		System.out.println("random suits: ");
		for (int i = 0; i < 6; i++) {
			System.out.println("  " + randomStrings.getRandomElement());
		}
		char[] suits = {'H', 'C', 'D', 'S'};
		RandomList<Card> randomCards = new RandomList<>();
		for (int i = 0; i < 4; i++) {
			for (int j = 1; j <= 13; j++) {
				randomCards.add(new Card(j, suits[i]));
			}
		}
		System.out.println("random cards: ");
		for (int i = 0; i < 20; i++) {
			System.out.print(" " + randomCards.getRandomElement());
		}
		System.out.println();
	}

}


