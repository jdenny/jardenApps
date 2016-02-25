package jarden.quiz;

public class AlgebraQuiz extends Quiz {

	public AlgebraQuiz() {
	}

	@Override
	public String getNextQuestion(int level) {
		int currMax = super.getMaxInt(level, 5);
		int correctAnswer = randomNum.nextInt(currMax);
		int a = randomNum.nextInt(currMax);
		int b = randomNum.nextInt(currMax);
		int c = randomNum.nextInt(currMax);
		if (c == a) {
			c++;
		}
		int d = (a - c) * correctAnswer + b;
		StringBuilder sb = new StringBuilder("solve: ");
		if (a == 0) {
			sb.append(b);
		} else {
			if (a == -1) {
				sb.append("-");
			} else if (a != 1) {
				sb.append(a);
			}
			sb.append("x");
			if (b != 0) {
				if (b > 0) {
					sb.append(" + " + b);
				} else {
					sb.append(" - " + (-b));
				}
			}
		}
		sb.append(" = ");
		if (c == 0) {
			sb.append(d);
		} else {
			if (c == -1) {
				sb.append("-");
			} else if (c != 1) {
				sb.append(c);
			}
			sb.append("x");
			if (d != 0) {
				if (d > 0) {
					sb.append(" + " + d);
				} else {
					sb.append(" - " + (-d));
				}
			}
		}
		//! sb.append("; x is ?");
		String question = sb.toString();
		super.setQuestionAnswer(question, correctAnswer);
		return question;
	}

	@Override
	public int isCorrect(String answer) {
		int answerInt = Integer.parseInt(answer);
		return super.isCorrect(answerInt);
	}

	@Override
	public int getAnswerType() {
		return Quiz.ANSWER_TYPE_INT;
	}
}
