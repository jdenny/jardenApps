package jarden.quiz;

public class AreasQuiz extends Quiz {
	private String hint;

	public AreasQuiz() {
	}
	
	@Override
	public String getNextQuestion(int level) {
		int currMax = super.getMaxInt(level, 5);
		int h = randomNum.nextInt(currMax) + 1;
		int w = randomNum.nextInt(currMax) + 1;
		int opCode = randomNum.nextInt(4);
		int correctAnswer;
		String question;

		if (opCode == 0) { // rectangle
			correctAnswer = h * w;
			question = "area of rectangle with height " +
				h + " and width " + w + "?";
			hint = "area of rectangle is width times height";
		}
		else if (opCode == 1) { // triangle
			if (h % 2 == 1 && w % 2 == 1) { // i.e. if both are odd
				++h;
			}
			correctAnswer = w * h / 2;
			question = "area of triangle with base " +
				h + " and height " + w + "?";
			hint = "area of triangle is half width times height";
		}
		else if (opCode == 2) { // parallelogram
			correctAnswer = h * w;
			question = "area of parallelogram with base " +
				h + " and height " + w + "?";
			hint = "area of parallelogram is width times height";
	    }
	    else { // opCode == 3: trapezium
	    	int w2 = randomNum.nextInt(currMax) + 1;
	    	if (h % 2 == 1) ++h; // ensure h is even
			correctAnswer = (w + w2) * h / 2;
			question = "area of trapezium with parallel sides " +
				w + " and " + w2 + " and height " + h + "?";
			hint = "area of trapezium half sum of parallel sides times height";
	    }
		super.setQuestionAnswer(question, correctAnswer);
		return question;
    }
	@Override
	public int getAnswerType() {
		return Quiz.ANSWER_TYPE_INT;
	}
	@Override
	public String getHint() {
		return hint;
	}
	@Override
	public int isCorrect(String answer) {
		int answerInt = Integer.parseInt(answer);
		return super.isCorrect(answerInt);
	}
}

