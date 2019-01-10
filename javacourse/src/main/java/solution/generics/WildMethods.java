package solution.generics;

import java.util.ArrayList;
import java.util.List;

public class WildMethods {

	public static void main(String[] args) {
		List<Integer> intlist = new ArrayList<>();
		intlist.add(13);
		intlist.add(12);
		intlist.add(-14);
		System.out.println("expected value: " + 2 + "; actual value: " + countEvens(intlist));
		List<Double> dublist = new ArrayList<>();
		dublist.add(3.4);
		dublist.add(2.7);
		dublist.add(-4.2);
		System.out.println("expected value: " + 2 + "; actual value: " + countEvens(dublist));
		List<String> wordList = new ArrayList<>();
		wordList.add("dad");
		wordList.add("madam");
		wordList.add("noon");
		wordList.add("palindrome");
		wordList.add("testset");
		System.out.println("expected value: " + 4 + "; actual value: " +
				countPalindromes(wordList));
		// no particular reason for using StringBuilder, other than it's a CharSequence
		List<StringBuilder> sbList = new ArrayList<>();
		sbList.add(new StringBuilder("minim"));
		sbList.add(new StringBuilder("mommy"));
		sbList.add(new StringBuilder("malayalam"));
		sbList.add(new StringBuilder("doom mood"));
		System.out.println("expected value: " + 3 + "; actual value: " +
				countPalindromes(sbList));
	}

	private static int countEvens(List<? extends Number> numbers) {
		int count = 0;
		for (Number number: numbers) {
			if ((number.intValue() % 2) == 0) ++count;
		}
		return count;
	}

 	private static int countPalindromes(List<? extends CharSequence> words) {
		int count = 0;
		for (CharSequence cs: words) {
			String word = cs.toString();
			int wordLength = word.length();
			boolean palindrome = true; // innocent until proven guilty
			for (int i = 0; i < (wordLength / 2); i++) {
				if (word.charAt(i) != word.charAt(wordLength -1 - i)) {
					palindrome = false;
					break;
				}
			}
			if (palindrome) ++count;
		}
		return count;
	}
}
