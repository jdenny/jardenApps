package solution.generics;

public class Card {
	private int rank;
	private char suit;
	
	public Card(int rank, char suit) {
		this.rank = rank;
		this.suit = suit;
	}
	public String toString() {
		String num;
		if (rank == 1) num = "A";
		else if (rank == 11) num = "J";
		else if (rank == 12) num = "Q";
		else if (rank == 13) num = "K";
		else num = Integer.toString(rank);
		return num + suit;
	}
}