package quiz;

import java.util.ArrayList;

public class CapitalsQuiz extends TextQAQuiz {
	private final static String[] COUNTRIES = {
		"Norway", "England", "France", "Italy", "Sweden",
		"Denmark", "Finland", "Netherlands", "Germany", "Spain",
		"Ireland", "Belgium", "Greece", "Portugal"
	};
	private final static String[] CAPITALS = {
		"Oslo", "London", "Paris", "Rome", "Stockholm",
		"Copenhagen", "Helsinki", "Amsterdam", "Berlin", "Madrid",
		"Dublin", "Brussels", "Athens", "Lisbon"	
	};
	private final static ArrayList<QuestionAnswer> qaList;
	private final static String questionTemplate = "what is the capital of {0}? ";
	
	static {
		qaList = new ArrayList<>();
		for (int i = 0; i < COUNTRIES.length; i++) {
			qaList.add(new QuestionAnswer(COUNTRIES[i], CAPITALS[i]));
		}
	}
	public CapitalsQuiz() {
		super(qaList, questionTemplate);
	}
}
