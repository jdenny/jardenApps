package jarden.quiz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Text-based question and answer quizzes.
 * Questions and answers can come from various sources, hence different constructors.
 * @author john.denny@gmail.com
 */
public class PresetQuiz extends Quiz {
	private List<QuestionAnswer> qaList;
    private int qaListIndex = -1; // used in learn mode
	private List<Integer> randomIndexList;
	private int randomListIndex = -1;
	private String questionTemplate = null;
	private String heading = null;

    // added for ReviseItQuiz:
    public enum QuizMode {
        LEARN, PRACTICE
    }
    private final static int TARGET_CORRECT_CT = 3;
    private QuizMode quizMode = QuizMode.PRACTICE;
    private List<Integer> failedIndexList = new LinkedList<>();
    private QuestionAnswer currentQA;
    private int consecutiveCorrects = 0;
    /*
       index of current question, which may have come from
          qaList[qaListIndex] (learn mode)
          or randomIndexList[randomListIndex] (practice mode)
          or failedIndexList[0]
     */
    private int currentQAIndex;

    /**
     * Build a Quiz from the InputStream. Assumes the inputStream contains
     * text in the form:
     *		Q: question1
     *		A: answer1
     *		Q: question2
     *	    F1: helpText2
     *		A: answer2
     *		etc
     *	    # comment line
     *	    QA: questionAnswer - e.g. question spoken, then player types the same
     *	    $IO: [questionStyle][answerStyle]
     *	    $T: template for question
     *	    $H: heading (or title)
     *
     * @param is an input stream containing the text
     * @param encoding e.g. "iso-8859-1"
     * @throws IOException
     * @see Quiz#getQuestionStyle()
     * @see #getNextQuestion(int)
     */
    public PresetQuiz(InputStream is, String encoding) throws IOException {
		this(new InputStreamReader(is, encoding));
	}
	public PresetQuiz(InputStreamReader isReader) throws IOException {
		BufferedReader reader = new BufferedReader(isReader);
		qaList = new ArrayList<>();
		String question = null;
		String answer;
		String helpText = null;
		while (true) {
			String line = reader.readLine();
			if (line == null) break; // end of file
			if (line.length() == 0 || line.startsWith("#")) continue;
			if (line.startsWith("Q: ")) {
				question = line.substring(3);
			} else if (line.startsWith("A: ")) {
				// if it's an old-fashioned multi-choice quiz, convert it to
				// simple QA, using only first answer:
				if (question != null) {
					answer = line.substring(3);
					qaList.add(new QuestionAnswer(question, answer, helpText));
					helpText = null;
					question = null;
				}
            } else if (line.startsWith("F1: ")) {
			    helpText = line.substring(4);
			} else if (line.startsWith("$T: ")) {
				questionTemplate = line.substring(4);
            } else if (line.startsWith("$H: ")) {
                heading = line.substring(4);
			} else if (line.startsWith("$IO: ")) {
				char questionStyle = line.charAt(5);
				char answerStyle = line.charAt(6);
				setQuestionStyle(questionStyle);
				setAnswerStyle(answerStyle);
			} else if (line.startsWith("QA: ")) {
				answer = line.substring(4);
				qaList.add(new QuestionAnswer(answer, answer));
			} else {
				System.out.println("unrecognised line: " + line);
			}
		}
		reader.close();
		reset();
	}
	/**
	 * Build a Quiz from properties, where for each property,
	 * name is the question, and value is the answer.
	 */
	public PresetQuiz(Properties properties) {
		Set<String> names = properties.stringPropertyNames();
		qaList = new ArrayList<>();
		for (String name: names) {
			String value = properties.getProperty(name);
			if (name.equals(Quiz.TEMPLATE_KEY)) {
				questionTemplate = value;
			} else if (name.equals(Quiz.IO_KEY)) {
				setQuestionStyle(value.charAt(0));
				setAnswerStyle(value.charAt(1));
			} else {
				qaList.add(new QuestionAnswer(name, value));
			}
		}
		reset();
	}
	/**
	 * Build a Quiz from a List of QuestionAnswer objects.
	 * QuestionAnswer has a constructor:
	 *    public QuestionAnswer(String question, String answer)
	 */
	public PresetQuiz(List<QuestionAnswer> qaList) {
		this(qaList, null);
	}
	/**
	 * Build a Quiz from a List of QuestionAnswer objects.
	 * QuestionAnswer has a constructor:
	 *    public QuestionAnswer(String question, String answer)
	 *    
	 * @param questionTemplate: String containing "{}" which is
	 * 		used in getNextQuestion()
	 * E.g. template is "what is the capital of {}?"
	 * 		question is "France"
	 * 		getNextQuestion() returns "what is the capital of France?"
	 */
	public PresetQuiz(List<QuestionAnswer> qaList, String questionTemplate) {
		this.qaList = qaList;
		this.questionTemplate = questionTemplate;
		reset();
	}
	public List<QuestionAnswer> getQuestionAnswerList() {
		return qaList;
	}
	public String getQuestionTemplate() {
		return questionTemplate;
	}
    public String getHeading() {
        return this.heading;
    }
	@Override
	public void reset() {
		super.reset();
		if (randomIndexList == null) {
            randomIndexList = new ArrayList<>();
            for (int i = 0; i < qaList.size(); i++) {
                randomIndexList.add(i);
            }
        }
		Collections.shuffle(randomIndexList);
        randomListIndex = 0;
	}
	/*!!
	@Override
	public String getNextQuestion(int level) throws EndOfQuestionsException {
		if (randomIndexList.size() == 0) {
			throw new EndOfQuestionsException("No more questions");
		}
		randomListIndex++;
		if (randomListIndex >= randomIndexList.size()) {
			randomListIndex = 0;
		}
		QuestionAnswer qa = qaList.get(randomIndexList.get(randomListIndex));
		String question;
		if (questionTemplate == null) {
			question = qa.question;
		} else {
			// I don't know how this ever worked, but it assumes the template contains {0}
			// whereas Capitals.properties, for example, contains {}
			// question = MessageFormat.format(questionTemplate, qa.question);
			question = questionTemplate.replace("{}", qa.question);
		}
		super.setQuestionAnswer(question, qa.answer);
		return question;
	}
	*/
	@Override
	public void notifyRightFirstTime() {
		if (randomListIndex >= 0) {
			randomIndexList.remove(randomListIndex);
			randomListIndex--; // otherwise we would miss out a question this time round
		}
	}
	@Override
	public int getAnswerType() {
		return Quiz.ANSWER_TYPE_STRING;
	}
	@Override
	public String getHint() {
		int len = getAttempts() * 2;
		String answer = super.getCorrectAnswer();
		if (len > answer.length()) {
			len = answer.length();
		}
		return answer.substring(0, len);
	}
	// Methods added for ReviseItQuiz
    /**
     * if QuizMode.LEARN:
     *    if no fails && end of currentQA: throw endOfQuestionsException
     *    if (3 consecutiveCorrects && fails) or end of current:
     *          get qaList[failedIndexList[0]]
     *    else: get qaList[qaListIndex]
     * if QuizMode.PRACTICE:
     *    if (3 consecutiveCorrects && fails): get qaList[failedIndexList[0]]
     *    else get qaList[randomIndexList[randomListIndex]]
     *
     * @return question string from current questionAnswer
     * @throws EndOfQuestionsException only applies to Learn mode
     */
    public String getNextQuestion(int level) throws EndOfQuestionsException {
        int failCt = getFailedCount();
        if (this.quizMode == QuizMode.LEARN) {
            int currentCt = getToDoCount() - 1; // haven't incremented qaListIndex yet
            if (failCt == 0 && currentCt == 0) {
                this.qaListIndex = -1;
                throw new EndOfQuestionsException();
            }
            if ((this.consecutiveCorrects >= TARGET_CORRECT_CT && failCt > 0) || currentCt == 0) {
                this.currentQA = getNextFail();
            } else {
                this.qaListIndex++;
                this.currentQA = this.qaList.get(qaListIndex);
                this.currentQAIndex = qaListIndex;
            }
        } else { // must be PRACTICE mode
            if (this.consecutiveCorrects >= TARGET_CORRECT_CT && failCt > 0) {
                this.currentQA = getNextFail();
            } else {
                ++this.randomListIndex;
                if (randomListIndex >= this.randomIndexList.size()) {
                    reset();
                }
                this.currentQAIndex = this.randomIndexList.get(randomListIndex);
                this.currentQA = this.qaList.get(currentQAIndex);
            }
        }
        String question = this.currentQA.question;
        if (questionTemplate != null) {
            question = questionTemplate.replace("{}", question);
        }
        super.setQuestionAnswer(currentQA.question, currentQA.answer);
        return question;
    }
    private QuestionAnswer getNextFail() {
        consecutiveCorrects = 0;
        this.currentQAIndex = this.failedIndexList.remove(0);
        return this.qaList.get(currentQAIndex);
    }
    public int getQuestionIndex() {
        return qaListIndex;
    }
    public List<Integer> getFailedIndexList() {
        return failedIndexList;
    }
    public QuestionAnswer getCurrentQuestionAnswer() {
        return currentQA;
    }
    public void setQuestionIndex(int qaListIndex) {
        // subtract 1, to repeat most recent question, not yet answered:
        int index = qaListIndex - 1;
        if (index >= qaList.size()) index = qaList.size() - 1;
        else if (index < -1) index = -1;
        this.qaListIndex = index;
    }
    public void setFailIndices(String[] failIndices) {
        for (String failIndex: failIndices) {
            failedIndexList.add(Integer.parseInt(failIndex));
        }
    }
    public void setQuizMode(QuizMode quizMode) {
        this.quizMode = quizMode;
    }
    public QuizMode getQuizMode() {
        return this.quizMode;
    }
    public int getToDoCount() {
        return this.qaList.size() - this.qaListIndex;
    }
    public int getCurrentQAIndex() {
        return this.currentQAIndex;
    }
    public void setCorrect(boolean correct) {
        if (correct) {
            this.consecutiveCorrects++;
        } else {
            consecutiveCorrects = 0;
            assert this.currentQAIndex >= 0: "currentQAIndex=" + currentQAIndex;
            this.failedIndexList.add(this.currentQAIndex);
        }
    }
    public int getFailedCount() {
        return this.failedIndexList.size();
    }
}
