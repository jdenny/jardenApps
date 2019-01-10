package demo.jdk5;

public class MyEnums {
	public final static int FS_HEARTS = 0;
	public final static int FS_CLUBS = 1;
	public final static int FS_DIAMONDS = 2;
	public final static int FS_SPADES = 3;
	
	public static void main(String[] args) {
		printSuit(SimpleSuit.CLUBS);
		System.out.println("Suits:");
		SimpleSuit[] values = SimpleSuit.values();
		for (SimpleSuit suit: values) {
			System.out.println("  " + suit.ordinal() + ": " + suit.name());
		}
		System.out.println("All suits:");
		for (Suit suit: Suit.values()) {
			System.out.println("  " + suit.getName() + ", " +
					suit.getShortName() + ", " + suit.toString());
		}
		ECard ecard10C = new ECard(10, Suit.CLUBS);
		printCard(ecard10C);
		ECard ecard5H = new ECard(5, Suit.HEARTS);
		printCard(ecard5H);
		System.out.println("10C > 5H = " + ecard5H.isBetter(ecard10C));
		System.out.println("2H > 5H = " + ecard5H.isBetter(new ECard(2, Suit.HEARTS)));
		System.out.println("KH > 5H = " + ecard5H.isBetter(new ECard(13, Suit.HEARTS)));
	}
	public static void printSuit(SimpleSuit suit) {
		System.out.println(suit);
	}
	private static void printCard(ECard ecard) {
		System.out.println(ecard + "; " + ecard.getShortName() +
				"; " + ecard.getNumber() + "; " + ecard.getSuit());
	}
}

enum SimpleSuit {
    HEARTS, CLUBS, DIAMONDS, SPADES; 
}

enum Suit {
	HEARTS("Hearts"),
	CLUBS("Clubs"),
	DIAMONDS("Diamonds"),
	SPADES("Spades");

	private final String shortName;
	private final String name;
	
	private Suit(String name) {
		this.name = name;
		this.shortName = name.substring(0, 1);
	}
	public String getName() {
		return name;
	}
	public String getShortName() {
		return shortName;
	}
}

class ECard {
	private Suit suit;
	private int number;
	private String value;
	private String shortValue;
	
	public ECard(int number, Suit suit) {
		if (number < 1 || number > 13) {
			throw new IllegalArgumentException("number " + number +
					" should be in range (1,13)");
		}
		this.suit = suit;
		this.number = number;
		String v;
		switch(number) {
		case 1:
			v = "A";
		case 11:
			v = "J";
		case 12:
			v = "Q";
		case 13:
			v = "K";
		default:
			v = Integer.toString(number);
		}
		this.value = v + " " + suit.getName(); 
		this.shortValue = v + suit.getShortName();
	}
	public Suit getSuit() {
		return suit;
	}
	public int getNumber() {
		return number;
	}
	public String toString() {
		return value;
	}
	public String getShortName() {
		return shortValue;
	}
	/*
	 * Returns true if secondCard is better than this card. 
	 */
	public boolean isBetter(ECard secondCard) {
		return (secondCard.suit == this.suit && secondCard.number > this.number);
	}
}

