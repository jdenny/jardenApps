package jarden.cards.test;

import jarden.cards.Card;
import jarden.cards.CardPack;
import jarden.cards.Hand;
import jarden.cards.Player;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestCardPack {
	private static CardPack cardPack;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		cardPack = new CardPack();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		cardPack = null;
	}

	@Test
	public final void test() {
		cardPack.shuffle();
		cardPack.deal();
		Hand hand = cardPack.getHand(Player.East);

		for (Card card: hand.cards) {
			System.out.print(card + " ");
		}
		System.out.println();
	}
	@Test
	public void getAllHands() {
		CardPack cardPack = new CardPack();
		cardPack.deal();
		for (Player player: Player.values()) {
			Hand hand = cardPack.getHand(player);
			System.out.print(player + ": ");
			printHand(hand);
		}
	}
	private static void printHand(Hand hand) {
		ArrayList<Card> cardList = hand.cards;
		for (Card card: cardList) {
			System.out.print(card + " ");
		}
		System.out.println();
	}

	private void printCards(Card[] cards) {
		for (Card card: cards) {
			System.out.print(card + " ");
		}
		System.out.println();
	}
	@Test
	public final void shouldSort() {
		CardPack pack2 = new CardPack();
		Card[] cards = cardPack.getCards();
		printCards(cards); // should be un-shuffled
		pack2.shuffle();
		cards = pack2.getCards();
		printCards(cards); // should be shuffled
		pack2.deal();
		ArrayList<Card> allCards = new ArrayList<Card>();
		Hand hand;
		for (Player player: Player.values()) {
			hand = pack2.getHand(player);
			allCards.addAll(hand.cards);
		}
		for (Card card: allCards) {
			System.out.print(card + " ");
		}
		System.out.println(); // sorted by hand, and concatenated
		Collections.sort(allCards); // now pack sorted
		for (Card card: allCards) {
			System.out.print(card + " ");
		}
		System.out.println();
		Assert.assertArrayEquals(allCards.toArray(new Card[0]), new CardPack().getCards());
		
	}

}
