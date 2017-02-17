package jarden.quiz;

/**
 * Simply generate a random number as the question. This is designed to be
 * spoken in selected language, as a means to learn to recognise numbers
 * spoken in that language.
 * @author john.denny@gmail.com
 *
 */
public class NumbersQuiz extends Quiz {
	private int previousAnswer = -999;
	
	public NumbersQuiz() {
		super();
		setQuestionStyle('S'); // Spoken Spanish
	}

	@Override
	public int getAnswerType() {
		return Quiz.ANSWER_TYPE_INT;
	}
	
	@Override
	public String getNextQuestion(int level) throws EndOfQuestionsException {
		int maxInt = super.getMaxInt(level);
		int correctAnswer = randomNum.nextInt(maxInt);  // i.e. allow zero
		if (correctAnswer == previousAnswer) {
			correctAnswer++;
		}
		this.previousAnswer = correctAnswer;
		String question = String.valueOf(correctAnswer);
		super.setQuestionAnswer(question, correctAnswer);
		return question;
	}
	
	@Override
	public int isCorrect(String answer) {
		int answerInt = Integer.parseInt(answer);
		return super.isCorrect(answerInt);
	}

}
