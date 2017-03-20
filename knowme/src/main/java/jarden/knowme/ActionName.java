package jarden.knowme;

// TODO: if we ever get KnowMeIF going again, put these in KnowMeIF
public interface ActionName {
	// actions used by KnowMeApp2&3&4: all sent from client to server
	String LINK_TO_SERVER_PLAYER = "linkToServerPlayer";
	String START = "start"; // not used in KnowMeApp3
	String NEXT_QUESTION = "nextQuestion";
	String NEXT_QUIZ = "nextQuiz";
	// responses used by KnowMeApp2&3; all sent from server to client
	String RESULT_TYPE = "resultType=";
	String RESULTS = "results";
	String QUESTION = "question";
	String END_OF_QUIZZES = "endOfQuizzes";
	String END_OF_QUESTIONS = "endOfQuestions";
	String OTHER_PLAYER_NAME = "otherPlayerName";

	// not yet used by KnowMeApp2:
	String LOGIN = "login";
	String NEW_PLAYER = "newPlayer";
	String REGISTER = "register";
	String NEW_FRIEND = "newFriend";
	String REQUEST_PAIRING = "requestPairing";
	String BACK_TO_FRIENDS = "backToFriends";
	String BACK_TO_QUESTION_TYPES = "backToQuestionTypes";
	String REFRESH_FRIENDS = "refreshFriends"; // or use timer
	String CHOOSE_QUESTION_SET = "chooseQuestionSet";
	String REFRESH_QUESTION_TYPES = "refreshQuestionTypes"; // or use timer
	String ANSWER = "answer";
	String REFRESH_WAIT_FOR_FRIEND = "refreshWaitForFriend"; // was refreshAnswers; or use timer 
	String LOGOUT = "logout";
	String CONTINUE_AFTER_RESULTS = "continueAfterResults";
	String REQUEST_FRIENDSHIP = "requestFriendship";
	String REFRESH_FRIENDSHIP = "refreshFriendship";
}

