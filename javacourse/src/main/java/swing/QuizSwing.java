package swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import quiz.AlgebraQuiz;
import quiz.ArithmeticQuiz;
import quiz.CapitalsQuiz;
import quiz.DBQuiz;
import quiz.EndOfQuestionsException;
import quiz.FileQuiz;
import quiz.Quiz;

/**
 * Swing GUI for the Quiz classes
 */
public class QuizSwing implements ActionListener {
	private JLabel questionLabel;
	private JLabel levelLabel;
	private JLabel rightLabel;
	private JLabel wrongLabel;
	private JLabel percentLabel;
	private JTextField answerField;
	private JTextField statusField;
	private ArithmeticQuiz arithmeticQuiz;
	private AlgebraQuiz algebraQuiz;
	private CapitalsQuiz capitalsQuiz;
	private FileQuiz fileQuiz;
	private DBQuiz spanishFoodQuiz;
	private DBQuiz spanishDrinkQuiz;
	private Quiz quiz;
	private int rightCt = 0;
	private int wrongCt = 0;

	public static void main(String[] args) throws EndOfQuestionsException {
		new QuizSwing();
	}
	public QuizSwing() throws EndOfQuestionsException {
		arithmeticQuiz = new ArithmeticQuiz(); 
		quiz = arithmeticQuiz; 
		String question = quiz.getNextQuestion();
		// create components:
		JFrame frame = new JFrame("QuizSwing");
		questionLabel = new JLabel(question);
		answerField = new JTextField(30);
		answerField.setActionCommand("Answer"); // for action handler
		statusField = new JTextField(30);
		levelLabel = new JLabel(Integer.toString(quiz.getLevel()));
		rightLabel = new JLabel("0");
		wrongLabel = new JLabel("0");
		percentLabel = new JLabel("0");
		JRadioButton arithmeticButton = new JRadioButton("Arithmetic");
		JRadioButton algebraButton = new JRadioButton("Algebra");
		JRadioButton capitalsButton = new JRadioButton("Capitals");
		JRadioButton fileButton = new JRadioButton("File");
		JRadioButton spanishFoodButton = new JRadioButton("Spanish Food");
		JRadioButton spanishDrinkButton = new JRadioButton("Spanish Drink");
		// set layout of components:
		Container container = frame.getContentPane();
		JPanel northPanel = new JPanel(new GridLayout(0, 1));
		northPanel.add(questionLabel);
		northPanel.add(answerField);
		JPanel levelPanel = new JPanel();
		levelPanel.add(new JLabel("Level: "));
		levelPanel.add(levelLabel);
		levelPanel.add(new JLabel("Right: "));
		levelPanel.add(rightLabel);
		levelPanel.add(new JLabel("Wrong: "));
		levelPanel.add(wrongLabel);
		levelPanel.add(new JLabel("Score%: "));
		levelPanel.add(percentLabel);
		northPanel.add(levelPanel);
		container.add(northPanel, BorderLayout.NORTH);
		container.add(statusField, BorderLayout.SOUTH);
		ButtonGroup group = new ButtonGroup();
		group.add(arithmeticButton);
		group.add(algebraButton);
		group.add(capitalsButton);
		group.add(fileButton);
		group.add(spanishFoodButton);
		group.add(spanishDrinkButton);
		JPanel radioPanel = new JPanel(new GridLayout(0, 1));
		radioPanel.add(arithmeticButton);
		radioPanel.add(algebraButton);
		radioPanel.add(capitalsButton);
		radioPanel.add(fileButton);
		radioPanel.add(spanishFoodButton);
		radioPanel.add(spanishDrinkButton);
		container.add(radioPanel, BorderLayout.CENTER);
		
		// add action handlers
		arithmeticButton.addActionListener(this);
		algebraButton.addActionListener(this);
		capitalsButton.addActionListener(this);
		fileButton.addActionListener(this);
		spanishFoodButton.addActionListener(this);
		spanishDrinkButton.addActionListener(this);
		answerField.addActionListener(this);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true); // start event handling thread
	}
	public void actionPerformed(ActionEvent event) {
		String action = event.getActionCommand();
		if (action.equals("Answer")) {
			String answer = answerField.getText();
			String resultStr;
			int result = quiz.isCorrect(answer);
			if (result == Quiz.INCORRECT) {
				++wrongCt;
				resultStr = "wrong; try again";
			} else {
				answerField.setText("");
				if (result == Quiz.CORRECT) {
					++rightCt;
					resultStr = "right";
				} else { // result must be FAIL
					++wrongCt;
					resultStr = "wrong! the right answer is: " + quiz.getAnswer();
				}
				try {
					questionLabel.setText(quiz.getNextQuestion());
				} catch (EndOfQuestionsException e) {
					questionLabel.setText(e.getMessage());
				}
			}
			statusField.setText(resultStr);
			levelLabel.setText(Integer.toString(quiz.getLevel()));
			rightLabel.setText(Integer.toString(rightCt));
			wrongLabel.setText(Integer.toString(wrongCt));
			percentLabel.setText(Integer.toString(rightCt * 100 / (rightCt + wrongCt)));
		} else if (action.equals("Arithmetic")) {
			quiz = new ArithmeticQuiz();
			reset();
		} else if (action.equals("Algebra")) {
			if (algebraQuiz == null) { algebraQuiz = new AlgebraQuiz(); }
			quiz = algebraQuiz;
			reset();
		} else if (action.equals("Capitals")) {
			if (capitalsQuiz == null) { capitalsQuiz = new CapitalsQuiz(); }
			quiz = capitalsQuiz;
			reset();
		} else if (action.equals("File")) {
			if (fileQuiz == null) { fileQuiz = new FileQuiz(); }
			quiz = fileQuiz;
			reset();
		} else if (action.equals("Spanish Food")) {
			if (spanishFoodQuiz == null) {
				spanishFoodQuiz = new DBQuiz("food");
			}
			quiz = spanishFoodQuiz;
			reset();
		} else if (action.equals("Spanish Drink")) {
			if (spanishDrinkQuiz == null) {
				spanishDrinkQuiz = new DBQuiz("drink");
			}
			quiz = spanishDrinkQuiz;
			reset();
		} else {
			statusField.setText("unknown action: " + action);
		}
	}
	private void reset() {
		rightCt = 0;
		wrongCt = 0;
		answerField.setText("");
		statusField.setText("new quiz type chosen");
		answerField.requestFocusInWindow();
		try {
			questionLabel.setText(quiz.getNextQuestion());
		} catch (EndOfQuestionsException e) {
			questionLabel.setText(e.getMessage());
		}
	}
}

