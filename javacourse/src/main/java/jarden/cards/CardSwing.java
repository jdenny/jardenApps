package jarden.cards;

import jarden.gui.GridBag;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * Simple Swing GUI for Card Application.
 */
public class CardSwing implements ActionListener {
	private CardPack cardPack;
	private HandPanel westPanel;
	private HandPanel eastPanel;
	private HandPanel northPanel;
	private HandPanel southPanel;
	private Font font;
	private JButton dealButton;
	private JRadioButton westEastRadio;
	private JRadioButton northSouthRadio;
	private JRadioButton allRadio;
	private JCheckBox westCheck;
	private JCheckBox eastCheck;
	private JCheckBox northCheck;
	private JCheckBox southCheck;

	public static void main(String[] args) {
		new CardSwing();
	}
	public CardSwing() {
		cardPack = new CardPack();
		// create components:
		JFrame frame = new JFrame("CardSwing");
		dealButton = new JButton("Deal");
		northSouthRadio = new JRadioButton("North/South");
		westEastRadio = new JRadioButton("West/East");
		westEastRadio.setSelected(true);
		allRadio = new JRadioButton("All");
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(northSouthRadio);
		buttonGroup.add(westEastRadio);
		buttonGroup.add(allRadio);
		westCheck = new JCheckBox("West");
		westCheck.setSelected(true);
		eastCheck = new JCheckBox("East");
		eastCheck.setSelected(true);
		northCheck = new JCheckBox("North");
		southCheck = new JCheckBox("South");
		String fontName = dealButton.getFont().getFontName();
		font = new Font(fontName, Font.PLAIN, 20);
		westPanel = new HandPanel(Player.West);
		eastPanel = new HandPanel(Player.East);
		southPanel = new HandPanel(Player.South);
		northPanel = new HandPanel(Player.North);

		// set layout of components:
		Container container = frame.getContentPane();
		JPanel controlPanel = new JPanel();
		controlPanel.add(allRadio);
		controlPanel.add(westEastRadio);
		controlPanel.add(northSouthRadio);
		controlPanel.add(westCheck);
		controlPanel.add(northCheck);
		controlPanel.add(eastCheck);
		controlPanel.add(southCheck);
		controlPanel.add(dealButton);
		container.add(controlPanel, BorderLayout.NORTH);
		JPanel handsPanel = new JPanel(new BorderLayout());
		container.add(handsPanel, BorderLayout.CENTER);
		handsPanel.add(westPanel, BorderLayout.WEST);
		handsPanel.add(eastPanel, BorderLayout.EAST);
		handsPanel.add(southPanel, BorderLayout.SOUTH);
		handsPanel.add(northPanel, BorderLayout.NORTH);
		// set event handlers:
		allRadio.addActionListener(this);
		westEastRadio.addActionListener(this);
		northSouthRadio.addActionListener(this);
		westCheck.addActionListener(this);
		northCheck.addActionListener(this);
		eastCheck.addActionListener(this);
		southCheck.addActionListener(this);
		dealButton.addActionListener(this);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getOurHands();
		showHands();
		frame.setSize(600, 500);
		frame.setVisible(true); // start event handling thread
	}
	class HandPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private Player player;
		private JLabel spadeValues;
		private JLabel heartValues;
		private JLabel diamondValues;
		private JLabel clubValues;
		
		public HandPanel(Player player) {
			this.player = player;
			JLabel playerLabel = new JLabel(player.toString());
			JLabel spadeIcon = new JLabel(String.valueOf(Card.ICON_SPADE));
			spadeIcon.setFont(font);
			spadeValues = new JLabel();
			spadeValues.setFont(font);
			JLabel heartIcon = new JLabel(String.valueOf(Card.ICON_HEART));
			heartIcon.setFont(font);
			heartIcon.setForeground(Color.red);
			heartValues = new JLabel();
			heartValues.setFont(font);
			JLabel diamondIcon = new JLabel(String.valueOf(Card.ICON_DIAMOND));
			diamondIcon.setFont(font);
			diamondIcon.setForeground(Color.red);
			diamondValues = new JLabel();
			diamondValues.setFont(font);
			JLabel clubIcon = new JLabel(String.valueOf(Card.ICON_CLUB));
			clubIcon.setFont(font);
			clubValues = new JLabel();
			clubValues.setFont(font);
			// set layout of components:
			GridBag gridBag = new GridBag(this);
			gridBag.add(playerLabel, 0, 0, 2, 1);
			gridBag.add(spadeIcon, 0, 1, 1, 1);
			gridBag.add(spadeValues, 1, 1);
			gridBag.add(heartIcon, 0, 2);
			gridBag.add(heartValues, 1, 2);
			gridBag.add(diamondIcon, 0, 3);
			gridBag.add(diamondValues, 1, 3);
			gridBag.add(clubIcon, 0, 4);
			gridBag.add(clubValues, 1, 4);
		}
		public void getHand() {
			Hand hand = cardPack.getHand(player);
			ArrayList<Card> cards = hand.cards;
			StringBuilder sb = new StringBuilder();
			int index = 0;
			int handSize = cards.size();
			Card card;
			while (index < handSize) {
				card = cards.get(index);
				if (!card.getSuit().equals(Suit.Spade)) break;
				sb.append(card.getShortRank() + " ");
				++index;
			}
			spadeValues.setText(sb.toString());
			sb.setLength(0);
			while (index < handSize) {
				card = cards.get(index);
				if (!card.getSuit().equals(Suit.Heart)) break;
				sb.append(card.getShortRank() + " ");
				++index;
			}
			heartValues.setText(sb.toString());
			sb.setLength(0);
			while (index < handSize) {
				card = cards.get(index);
				if (!card.getSuit().equals(Suit.Diamond)) break;
				sb.append(card.getShortRank() + " ");
				++index;
			}
			diamondValues.setText(sb.toString());
			sb.setLength(0);
			while (index < handSize) {
				card = cards.get(index);
				if (!card.getSuit().equals(Suit.Club)) break;
				sb.append(card.getShortRank() + " ");
				++index;
			}
			clubValues.setText(sb.toString());
		}
	}
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if (source == dealButton) {
			getOurHands();
		} else if (source == this.allRadio) {
			westCheck.setSelected(true);
			eastCheck.setSelected(true);
			northCheck.setSelected(true);
			southCheck.setSelected(true);
		} else if (source == this.westEastRadio) {
			westCheck.setSelected(true);
			eastCheck.setSelected(true);
			northCheck.setSelected(false);
			southCheck.setSelected(false);
		} else if (source == this.northSouthRadio) {
			westCheck.setSelected(false);
			eastCheck.setSelected(false);
			northCheck.setSelected(true);
			southCheck.setSelected(true);
		}
		showHands();
	}
	private void showHands() {
		westPanel.setVisible(westCheck.isSelected());
		eastPanel.setVisible(eastCheck.isSelected());
		northPanel.setVisible(northCheck.isSelected());
		southPanel.setVisible(southCheck.isSelected());
	}
	private void getOurHands() {
		cardPack.shuffle();
		cardPack.deal();
		westPanel.getHand();
		eastPanel.getHand();
		southPanel.getHand();
		northPanel.getHand();
	}
}

