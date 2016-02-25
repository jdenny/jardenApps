package jarden.quiz;

/**
 * Convert between fraction, decimal and percentage.
 * Q/A to 2 decimal places.
 * Calculations to 3 places; round by adding 0.005; hold to 2 places. e.g.
 * number		3-places	rounded	2-places
 * ------		--------	-------	--------
 * 1.33333		1.333		1.338	1.33
 * 2.66666		2.666		2.671	2.67
 * 1.11499		1.114		1.119	1.11
 * 1.11500		1.115		1.120	1.12
 */
public class FractionsQuiz extends Quiz {
	private final static int FRACTION_TO_DECIMAL = 0;
	private final static int FRACTION_TO_PERCENTAGE = 1;
	private final static int DECIMAL_TO_PERCENTAGE = 2;
	private final static int PERCENTAGE_TO_FRACTION = 3;
	private final static int DECIMAL_TO_FRACTION = 4;
	// private final static int PERCENTAGE_TO_DECIMAL = 5;
	private int type;

	public FractionsQuiz() {
	}
	@Override
	public int isCorrect(String answer) throws NumberFormatException {
		if (type == FRACTION_TO_PERCENTAGE || type == DECIMAL_TO_PERCENTAGE) {
			int index = answer.indexOf('%');
			if (index >= 0) answer = answer.substring(0, index);
			return super.isCorrect(Integer.parseInt(answer));
		}
		int resX100;
		if (type == PERCENTAGE_TO_FRACTION || type == DECIMAL_TO_FRACTION) {
			int index = answer.indexOf('/');
			if (index < 0) {
				resX100 = Integer.parseInt(answer) * 100;
			}
			else {
				int a = Integer.parseInt(answer.substring(0, index));
				int b = Integer.parseInt(answer.substring(index + 1));
				resX100 = ((a * 1000 / b) + 5) / 10;
			}
		}
		else { // type x_TO_DECIMAL
			double res = Double.parseDouble(answer);
			resX100 = (int)((res * 1000 + 5) / 10);
		}
		return super.isCorrect(resX100);
	}
	public String getNextQuestion(int level) {
		int currMax = super.getMaxInt(level, 5);
		type = randomNum.nextInt(6);
		System.out.println("type=" + type);
		String question;
		int correctAnswer = 0;
		int a = randomNum.nextInt(currMax);
		int b = randomNum.nextInt(currMax);
		if (a > b) {
			int temp = a;
			a = b;
			b = temp;
		}
		++b; // make sure no divide by zero
		if (type == FRACTION_TO_DECIMAL) {
			correctAnswer = ((a * 1000 / b) + 5) / 10;
			question = "what is " + a + "/" + b +
				" as a decimal (2 decimal places)?";
		}
		else if (type == FRACTION_TO_PERCENTAGE) {
			correctAnswer = ((a * 1000 / b) + 5) / 10;
			question = "what is " + a + "/" + b +
				" as a percentage?";
		}
		else if (type == DECIMAL_TO_PERCENTAGE) {
			correctAnswer = ((a * 1000 / b) + 5) / 10;
			question = "what is " + (correctAnswer / 100.0) +
				" as a percentage?";
		}
		else if (type == PERCENTAGE_TO_FRACTION) {
			correctAnswer = ((a * 1000 / b) + 5) / 10;
			question = "what is " + correctAnswer +
				"% as a fraction (a / b)?";
		}
		else if (type == DECIMAL_TO_FRACTION) {
			correctAnswer = ((a * 1000 / b) + 5) / 10;
			question = "what is " + (correctAnswer / 100.0) +
				" as a fraction (a / b)?";
		}
		else { // percentage to decimal
			correctAnswer = ((a * 1000 / b) + 5) / 10;
			question = "what is " + correctAnswer + "%" +
				" as a decimal (2 decimal places)?";
		}
		super.setQuestionAnswer(question, correctAnswer);
		return question;
	}
	
	@Override
	public int getAnswerType() {
		return Quiz.ANSWER_TYPE_DOUBLE;
	}
}
