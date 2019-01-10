package first;

import java.util.HashMap;

public class Plurals {
	private static String[][] irregNouns = {
		{"cattle", "cattle"},
	    {"fox", "foxes"},
		{"man", "men"},
	    {"ox", "oxen"},
	    {"sheep", "sheep"},
	    {"woman", "women"}
	};
	private HashMap<String, String> pluralMap;
	
	public Plurals() {
		pluralMap = new HashMap<>();
		for (String[] pair: irregNouns) {
			pluralMap.put(pair[0], pair[1]);
		}
	}
	public void addPlural(String singular, String plural) {
		pluralMap.put(singular, plural);
	}
	public String findPlural2(String noun) {
		String plural = this.pluralMap.get(noun);
		if (plural != null) {
			return plural;
		}
		else {
			return noun + "s";
		}
	}

	public static String findPlural(String noun) {
		for (int i = 0; i < irregNouns.length; i++) {
			if (irregNouns[i][0].equals(noun)) {
				return irregNouns[i][1];
			}
		}
		return noun + "s";
	}

	public static void main(String[] args) {
	    String[] nouns = {"man", "boy", "fox", "dog", "woman", "girl",
	    		"ox", "sheep"};
	    for (String noun: nouns) {
	        System.out.println(findPlural(noun));
	    }
	    Plurals plurals = new Plurals();
	    System.out.println("plural of child is " + plurals.findPlural2("child"));
	    plurals.addPlural("child", "children");
	    System.out.println("plural of child is " + plurals.findPlural2("child"));
	    System.out.println("el final");
	}
}
