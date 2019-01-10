package jarden.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Simple user interface to a chess piece. Only shows one piece at a time!
 */
public class ChessSwing implements ActionListener, ListSelectionListener {
	private JTextField messageField;
	private JButton[] squares;
	private Chess chessPiece = null;
	private JComboBox<String> pieceList;
	private String[] pieceNames;
	private Chess[] chessPieces;

	public ChessSwing(Chess[] chessPieces) {
		this.chessPieces = chessPieces;
		pieceNames = new String[chessPieces.length];
		for (int i = 0; i < chessPieces.length; i++) {
			Chess chess = chessPieces[i];
			pieceNames[i] = chess.getName();
		}
		JFrame frame = new JFrame("ChessSwing");
		this.messageField = new JTextField();
		this.squares = new JButton[100];
		JPanel squaresPanel = new JPanel();
		squaresPanel.setLayout(new GridLayout(10, 10));
		Font buttonFont = new Font(Font.MONOSPACED, Font.BOLD, 24);
		for (int i = 0; i < squares.length; i++) {
			JButton button = new JButton();
			button.setPreferredSize(new Dimension(60, 60));
			button.setFont(buttonFont);
			if (i < 10 || i > 89 || i%10 == 0 || i%10 == 9) {
				button.setBackground(Color.gray);
			} else if (i/10%2 == 0 && i%2 == 0 || i/10%2 == 1 && i%2 == 1) { // wowabaweeba!
				button.setBackground(Color.lightGray);
			} else {
				button.setBackground(Color.white);
			}
			button.setActionCommand(String.valueOf(i));
			this.squares[i] = button;
			squaresPanel.add(button);
			button.addActionListener(this);
		}
		this.pieceList = new JComboBox<String>(pieceNames);
		this.pieceList.addActionListener(this);
		Container container = frame.getContentPane();
		container.add(squaresPanel, BorderLayout.CENTER);
		container.add(pieceList, BorderLayout.NORTH);
		container.add(messageField, BorderLayout.SOUTH);
		setChessPiece(0);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true); // start event handling thread
	}
	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == pieceList) {
			int index = pieceList.getSelectedIndex();
			this.setChessPiece(index);
		} else {
			String action = event.getActionCommand();
			int index = Integer.parseInt(action);
			int x = index % 10;
			int y = 9 - index/10;
			try {
				removeFromBoard(chessPiece);
				chessPiece.move(x, y);
				messageField.setText("");
			} catch (IllegalArgumentException e) {
				messageField.setText(e.getMessage());
			}
			showOnBoard(chessPiece);
		}
	}
	@Override
	public void valueChanged(ListSelectionEvent event) {
	}
	private void showOnBoard(Chess chess) {
		int index = getBoardIndex(chess);
		squares[index].setText(chess.getShortName());
	}
	private int getBoardIndex(Chess chess) {
		int x = chess.getXPos();
		int y = chess.getYPos();
		return (9-y)*10 + x;
	}
	private void removeFromBoard(Chess chess) {
		int index = getBoardIndex(chess);
		squares[index].setText("");
	}
	private void setChessPiece(int index) {
		if (this.chessPiece != null) {
			removeFromBoard(this.chessPiece);
		}
		this.chessPiece = chessPieces[index];
		showOnBoard(this.chessPiece);
	}
}

