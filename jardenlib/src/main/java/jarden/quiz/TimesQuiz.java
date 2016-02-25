package jarden.quiz;

public class TimesQuiz extends ArithmeticQuiz {

	public TimesQuiz() {
	}
	public String getNextQuestion(int level) {
		int maxInt = super.getMaxInt(level, 5);
		int a = randomNum.nextInt(maxInt) + 1;
		int b = randomNum.nextInt(maxInt) + 1;
		String question = "what is " + a + " times " + b + "?";
		super.setQuestionAnswer(question, a * b);
		return question;
	}
}
