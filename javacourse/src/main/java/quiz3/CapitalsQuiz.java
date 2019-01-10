package quiz3;

public class CapitalsQuiz extends Quiz {
	private String[] countries = {
		"Norway", "England", "France", "Italy", "Sweden",
		"Denmark", "Finland", "Netherlands", "Germany", "Spain",
		"Ireland", "Belgium", "Greece", "Portugal"
	};
	private String[] capitals = {
		"Oslo", "London", "Paris", "Rome", "Stockholm",
		"Copenhagen", "Helsinki", "Amsterdam", "Berlin", "Madrid",
		"Dublin", "Brussels", "Athens", "Lisbon"	
	};
	private int index = -1;
	
	public CapitalsQuiz() {
	}
	@Override
	public String getNextQuestion() {
		if (index < (countries.length-1)) {
			index++;
		}
		String question = "what is the capital of " + countries[index] + "? ";
		super.setQuestionAnswer(question, capitals[index]);
		return question;
	}
}
