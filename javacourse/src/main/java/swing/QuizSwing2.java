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
 * Swing GUI for the Quiz classes.
 * Same as QuizSwing, except the only mention of the different Quiz classes
 * is in the array at the beginning; hence, to add a new type, simply add
 * to this array.
 */
public class QuizSwing2 implements ActionListener {
	private final static Class<?>[] quizClasses = {
		ArithmeticQuiz.class, AlgebraQuiz.class, CapitalsQuiz.class,
		FileQuiz.class, DBQuiz.class
	};
	private JLabel questionLabel;
	private JLabel levelLabel;
	private JLabel rightLabel;
	private JLabel wrongLabel;
	private JLabel percentLabel;
	private JTextField answerField;
	private JTextField statusField;
	private Quiz quiz;
	private int rightCt = 0;
	private int wrongCt = 0;

	public static void main(String[] args) throws EndOfQuestionsException,
			InstantiationException, IllegalAccessException {
		new QuizSwing2();
	}
	public QuizSwing2() throws EndOfQuestionsException,
			InstantiationException, IllegalAccessException {
		quiz = (Quiz)quizClasses[0].newInstance();
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
		JPanel radioPanel = new JPanel(new GridLayout(0, 1));
		container.add(radioPanel, BorderLayout.CENTER);
		
		// add action handlers
		answerField.addActionListener(this);
		
		// now deal with Quiz Classes:
		for (Class<?> clas: quizClasses) {
			String className = clas.getName();
			int lastDot = className.lastIndexOf('.');
			if (lastDot >= 0) {
				className = className.substring(lastDot + 1);
			}
			System.out.println("class name = " + className);
			JRadioButton quizTypeButton = new JRadioButton(className);
			group.add(quizTypeButton);
			radioPanel.add(quizTypeButton);
			quizTypeButton.addActionListener(this);
		}
		
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
		} else { // new Quiz type selected
			for (Class<?> clas: quizClasses) {
				String className = clas.getName();
				int lastDot = className.lastIndexOf('.');
				if (lastDot >= 0) {
					className = className.substring(lastDot + 1);
				}
				if (action.equals(className)) {
					try {
						quiz = (Quiz)clas.newInstance();
					} catch (InstantiationException | IllegalAccessException e) {
						e.printStackTrace();
					}
					reset();
				}
			}
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

