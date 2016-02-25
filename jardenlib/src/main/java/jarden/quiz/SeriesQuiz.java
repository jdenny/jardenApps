package jarden.quiz;

import jarden.maths.Maths;

/**
 * Produce question of form: find nth term in series.
 * Lower levels, ask for next in series; later ask for next plus 1;
 * finally ask for nth.
 * According to Sam's workbook, there are six different types of sequence!
 * My guess is: even, odd, square, cube, triangle, prime.
 * Triangle numbers are: 1, 3, 6, 10, 15, 21
 * In this case, formulae are:
 *		tn = an + b (evens: 2n; odds: 2n - 1)
 *		tn = a**n + b (squares: n**2; cubes: n**3)
 *		primes: no formula!
 *		tn = an**2 + bn + c (triangle numbers above: 0.5n**2 + 0.5n + 0)
 */
public class SeriesQuiz extends Quiz {
	private Maths maths;
	private String hint;

	public SeriesQuiz() {
		maths = new Maths();
	}
	@Override
	public String getNextQuestion(int level) {
		int maxInt = super.getMaxInt(level, 5);
		int opCode = randomNum.nextInt(4);
		int a = randomNum.nextInt(maxInt) + 1;
		int b = randomNum.nextInt(maxInt) + 1;
		int c = randomNum.nextInt(maxInt) + 1;
		int correctAnswer;
		String question;
		if (opCode == 0) {
			hint = "a * n + b";
			int t1 = a + b;
			int t2 = a * 2 + b;
			int t3 = a * 3 + b;
			int t4 = a * 4 + b;
			correctAnswer = a * 5 + b;
			question = t1 + ", " + t2 + ", " + t3 + ", " + t4;
		}
		else if (opCode == 1) {
			hint = "(a + n)**2 + b";
			int t1 = (a + 1)*(a + 1) + b;
			int t2 = (a + 2)*(a + 2) + b;
			int t3 = (a + 3)*(a + 3) + b;
			int t4 = (a + 4)*(a + 4) + b;
			correctAnswer = (a + 5)*(a + 5) + b;
			question = t1 + ", " + t2 + ", " + t3 + ", " + t4;
		}
		else if (opCode == 2) {
			hint = "prime numbers";
			long[] primes = maths.getPrimes(1, (5 + a), 0, false, false);
			correctAnswer = (int)primes[a + 4];
			question = primes[a] + ", " +
				primes[a+1] + ", " + primes[a+2] + ", " + primes[a+3];
		}
		else {
			hint = "an**2 + bn + c";
			int t1 = (a + 1)*(a + 1) + b + c;
			int t2 = (a + 2)*(a + 2) + b * 2 + c;
			int t3 = (a + 3)*(a + 3) + b * 3 + c;
			int t4 = (a + 4)*(a + 4) + b * 4 + c;
			correctAnswer = (a + 5)*(a + 5) + b * 5 + c;
			question = t1 + ", " + t2 + ", " + t3 + ", " + t4;
		}
		question = question + "; next in series?";
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
	
	@Override
	public String getHint() {
		return hint;
	}
}
