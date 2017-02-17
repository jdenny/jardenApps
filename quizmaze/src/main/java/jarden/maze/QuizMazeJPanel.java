package jarden.maze;

import jarden.awt.GridBag;
import jarden.clock.ClockListener;
import jarden.quiz.AmazeQuizCache;
import jarden.quiz.EndOfQuestionsException;
import jarden.quiz.PresetQuiz;
import jarden.quiz.QuestionAnswer;
import jarden.quiz.Quiz;
import jarden.quiz.QuizCache;
import jarden.quiz.QuizCacheListener;
import jarden.quiz.QuizListener;

import java.applet.AudioClip;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/*
 * TODO: make this more like structure of QuizApp:
 * 		Take most of logic out of MazeCanvas, to make it like MazeView
 * 		Make this class similar structure to QAFragment and MazeFragment
 * TODO: check how many times reset() methods are called: may be duplicates!
 */
public class QuizMazeJPanel extends JPanel implements ClockListener, MazeListener,
		QuizListener, ActionListener, ItemListener, KeyListener, QuizCacheListener {
	private static final long serialVersionUID = 1L;
	private final static int timePenalty = 5;
	private final static int timeBonus = 30;
	private final static int baddySleepTenths = 50;

	private QuizCache quizCache;

	// user interface components:
	private JLabel questionLabel;
	private JLabel statusLabel;
	private JTextField answerField;
	private int level = 1;
	private JLabel levelLabel;
	private JButton resetButt;
	private JComboBox<String> quizTypeChoice;
	private JComboBox<String> quizSubtypeChoice;
	private JTextArea highScoresList;
	private JButton highScoreButton;
	private JTextField userNameField;
	private QuizClock quizClock;
	private MazeCanvas mazeCanvas;
	private AudioClip audioCorrect;
	private AudioClip audioWrong;
	private AudioClip audioTick;
	private AudioClip audioTimeout;
	private AudioClip audioLookout;
	private Font mazeFont;
	// end of interface components
	private Quiz quiz;
	private int quizAnswerType;
	private String questionString;
	private boolean finished;
	private String quizSubtype;
	private String quizType;
	private boolean debug = false;

	public QuizMazeJPanel() {
		quizCache = new AmazeQuizCache(this);

		// GUI create components:
		setLayout(new BorderLayout());
		mazeFont = new Font("Helvetica", Font.PLAIN, 16);

		JPanel northPan = new JPanel();
		northPan.setLayout(new GridLayout(3, 1));
		questionLabel = new JLabel();
		questionLabel.setFont(mazeFont);
		northPan.add(questionLabel);
		answerField = new JTextField("", 40);
		answerField.setFont(mazeFont);
		northPan.add(answerField);
		statusLabel = new JLabel();
		statusLabel.setFont(mazeFont);
		northPan.add(statusLabel);
		add("North", northPan);

		JPanel southPan = new JPanel();
		add("South", southPan);
		mazeCanvas = new MazeCanvas(this, 14, 10, baddySleepTenths);
		southPan.add(mazeCanvas);
		quizClock = new QuizClock(80, this);
		resetButt = new JButton("Reset");
		quizTypeChoice = new JComboBox<String>(QuizCache.quizTypes);
		quizSubtypeChoice = new JComboBox<String>();
		levelLabel = new JLabel("Level: " + level);
		userNameField = new JTextField(10);
		highScoreButton = new JButton("High Score");
		highScoresList = new JTextArea(6, 20);
		JScrollPane areaScrollPane = new JScrollPane(highScoresList);
		// areaScrollPane.setPreferredSize(new Dimension(250, 250));
		// layout of control area:
		JPanel southEastPan = new JPanel();
		GridBag gridBag = new GridBag(southEastPan);
		southPan.add(southEastPan);
		gridBag.add(quizClock, 0, 0, 1, 3);
		gridBag.add(resetButt, 1, 0, 1, 1);
		gridBag.add(quizTypeChoice, 2, 0, 1, 1);
		gridBag.add(quizSubtypeChoice, 1, 1, 2, 1);
		gridBag.add(levelLabel, 1, 2, 1, 1);
		// in eclipse: run configurations, vm arguments, -Ddebug=true
		String debugStr = System.getProperty("debug");
		debug = (debugStr != null);
		showStatus("loading database");
		loadDatabase();
		showStatus("");
		JPanel namePanel = new JPanel();
		gridBag.add(namePanel, 0, 3, 3, 1);
		namePanel.add(new JLabel("Name"));
		namePanel.add(userNameField);
		namePanel.add(highScoreButton);
		gridBag.fill = GridBag.BOTH;
		gridBag.weightx = 0.0;
		gridBag.weighty = 0.0;
		gridBag.add(areaScrollPane, 0, 4, 3, 1);
		// event listeners
		highScoreButton.addActionListener(this);
		answerField.addActionListener(this);
		quizTypeChoice.addActionListener(this);
		quizSubtypeChoice.addItemListener(this);
		resetButt.addActionListener(this);
		answerField.addKeyListener(this);
	}

	public void reset() {
		level -= 2;
		if (level < 1)
			level = 1;
		quiz.reset();
		reset2();
	}

	private void reset2() {
		levelLabel.setText("Level: " + level);
		int sleepTime = (int) (baddySleepTenths / (1 + 0.5 * level));
		mazeCanvas.setBaddySleep(sleepTime);
		nextQuestion();
		finished = false;
		mazeCanvas.reset();
		quizClock.reset();
	}

	private void nextQuestion() {
		try {
			questionString = quiz.getNextQuestion(level);
			questionLabel.setText(questionString);
			answerField.setText("");
			answerField.requestFocus();
		} catch (EndOfQuestionsException eqe) {
			questionLabel.setText("You've answered all the questions!");
			finish();
		}
	}

	private void setHighScore(String name) throws IOException {
		highScoresList.setText("");
		ArrayList<String> highScores = quizCache.setHighScore(name, level, quizSubtype);
		for (String highScore: highScores) {
			highScoresList.append(highScore + "\n");
		}
	}

	private void addFailure() {
		if (quiz instanceof PresetQuiz) {
			QuestionAnswer qa = new QuestionAnswer(
					quiz.getCurrentQuestion(), quiz.getCorrectAnswer());
			quizCache.addFailure(qa);
		}
	}

	private void checkAnswer() {
		String answer = answerField.getText().trim();
		if (answer.length() == 0) {
			showStatus("please supply an answer");
			return;
		}
		String status;
		int result = quiz.isCorrect(answer);
		if (result == Quiz.INCORRECT) {
			status = "Wrong!";
			addFailure();
			String hint = quiz.getHint();
			if (hint != null && hint.length() > 0) {
				status += " Hint: " + hint;
			}
			if (quizAnswerType == Quiz.ANSWER_TYPE_INT) {
				// for numeric answers, clear answer field, but show
				// old value in status
				status = answer + " " + status;
				answerField.setText("");
			}
			audioWrong.play();
			quizClock.adjust(-timePenalty);
		} else {
			if (result == Quiz.CORRECT) {
				status = "That's right!";
				audioCorrect.play();
				quizClock.adjust(timeBonus);
			} else { // result must be FAIL
				status = quiz.getCurrentQuestion() +
						" Answer: " + quiz.getCorrectAnswer();
				audioWrong.play();
				quizClock.adjust(-timePenalty);
			}
			nextQuestion();
		}
		showStatus(status);
		answerField.requestFocus();
	}

	private void finish() {
		quizClock.stop();
		mazeCanvas.stop();
		finished = true;
		showStatus("Finished");
	}
	
	private void loadQuizSubtypeChoices() throws IOException {
		quizType = (String) quizTypeChoice.getSelectedItem();
		String[] subtypeNames = quizCache.getSubtypeNames(quizType);
		this.highScoreButton.setEnabled(quizType.equals("Maths"));
		// now that we've got our array of subtypeNames (which may have been
		// an asynchronous action) we can start updating the AWT components,
		// which will in turn cause AWT events
		quizSubtypeChoice.removeAllItems();
		for (String subtype: subtypeNames) {
			quizSubtypeChoice.addItem(subtype);	
		}
	}

	private void loadDatabase() {
		try {
			ClassLoader cl = this.getClass().getClassLoader();
			audioCorrect = (AudioClip) cl.getResource("sounds/enter.au").getContent();
			audioLookout = (AudioClip) cl.getResource("sounds/Lookout.au").getContent();
			audioWrong = (AudioClip) cl.getResource("sounds/ricochet.au").getContent();
			audioTick = (AudioClip) cl.getResource("sounds/Beep.au").getContent();
			audioTimeout = (AudioClip) cl.getResource("sounds/Crash.au").getContent();

			loadQuizSubtypeChoices();
			setQuizSubtype((String) quizSubtypeChoice.getSelectedItem());
		} catch (Exception e) {
			showStatus("loadDatabase: " + e);
			e.printStackTrace();
		}
	}
	
	public void showStatus(String message) {
		// System.out.println("showStatus: " + errText);
		statusLabel.setText(message);
	}

	@Override
	public void onClockTick() {
		audioTick.play();
	}

	@Override
	public void onLost() {
		audioTimeout.play();
		questionLabel.setText("You've lost!");
		finish();
	}

	@Override
	public void itemStateChanged(ItemEvent event) {
		if (debug) {
			System.out.print("quizMazeJPanel.itemStateChanged(); ");
			System.out.printf(
					"id=%d; statechange=%d, selected=%b%n",
					event.getID(), event.getStateChange(),
					event.getStateChange() == ItemEvent.SELECTED);
		}
		if (event.getSource() == quizSubtypeChoice) {
			String quizST = (String) quizSubtypeChoice.getSelectedItem();
			// load new quiz only if user has selected quiz subtype,
			// but not if subtype is being reset following change of quizType
			if (quizST != null && (!quizST.equals(quizSubtype))
					&& event.getStateChange() == ItemEvent.SELECTED) {
				setQuizSubtype(quizST);
			}
		}
	}

	private void setQuizSubtype(String quizSubtype) {
		this.quizSubtype = quizSubtype;
		try {
			this.quiz = quizCache.getQuiz(quizType, quizSubtype);
			this.quizAnswerType = quiz.getAnswerType();
			quiz.setQuizListener(this);
			if (quizType.equals("Maths")) {
				// get high scores, but don't set a new one
				try {
					setHighScore(null);
				} catch (IOException e) {
					showStatus("unable to get high scores");
					System.out.println("unable to get high scores: " + e);
				}
			}
			level = 1;
			reset();
		} catch (IOException e) {
			showStatus(e.toString());
			e.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == answerField) {
			checkAnswer();
		} else if (event.getSource() == resetButt) {
			reset();
		} else if (event.getSource() == highScoreButton) {
			String name = userNameField.getText();
			if (name.length() < 1) {
				showStatus("no name supplied!");
			} else if (level < 2) {
				showStatus("get past level 1 first!");
			} else {
				try {
					setHighScore(name);
				} catch (IOException ioe) {
					showStatus("io exception: " + ioe);
					return;
				}
			}
		} else if (event.getSource() == quizTypeChoice) {
			try {
				loadQuizSubtypeChoices();
			} catch (IOException ioe) {
				showStatus("io exception: " + ioe);
				ioe.printStackTrace();
			}
		}
	}

	public void keyPressed(KeyEvent event) {
		int keyCode = event.getKeyCode();
		switch (keyCode) {
		case KeyEvent.VK_UP:
		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_RIGHT:
			if (!finished) {
				mazeCanvas.moveMe(keyCode);
			}
		}
	}

	public void keyReleased(KeyEvent event) {
	}

	public void keyTyped(KeyEvent event) {
	}

	@Override
	public int onNextLevel() {
		++this.level;
		reset2();
		return this.level;
	}

	@Override
	public void onLookOut() {
		audioLookout.play();
	}

	@Override
	public int getLevel() {
		return this.level;
	}

	@Override
	public void onRightAnswer() {
	}

	@Override
	public void onWrongAnswer() {
	}

	@Override
	public void onThreeRightFirstTime() {
		mazeCanvas.giveKey();
	}

	@Override
	public void onReset() {
	}

	@Override
	public void onLogMessage(String message) {
		this.showStatus(message);
	}

	@Override
	public void onEndOfQuestions() {
		showStatus("End of questions");
	}

}
