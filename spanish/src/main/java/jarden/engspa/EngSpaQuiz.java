package jarden.engspa;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import jarden.engspa.VerbUtils.Person;
import jarden.engspa.VerbUtils.Tense;
import jarden.provider.engspa.EngSpaContract.QAStyle;
import jarden.provider.engspa.EngSpaContract.Qualifier;
import jarden.provider.engspa.EngSpaContract.WordType;
import jarden.quiz.EndOfQuestionsException;
import jarden.quiz.Quiz;

/*
 * TODO: maybe make this more like Quiz, where the UI holds the level
 * and passes it to us. Also note that Quiz already has
 * questionStyle & answerStyle: printed or spoken; see EngSpaFragment.currentQuestionStyle
 */
public class EngSpaQuiz extends Quiz {
	public interface QuizEventListener {
		void onNewLevel();
		void onTopicComplete();
	}
	public static final int WORDS_PER_LEVEL = 10;
	public static final int USER_LEVEL_ALL = 12345;

	/*
	 * controls which list of words to choose the next question from,
	 * one of current, fails and passed
	 */
	private static final char[] NORMAL_CFP_LIST = {'C', 'F', 'P', 'C', 'F'};
	private static final char[] ALL_LEVELS_CFP_LIST = {'F', 'P'};
	private char[] cfpList;
	
	private String spanish;
	private String english;
	private String topic = null;
	
	private Random random = new Random();
	private static final Person[] persons;
	private static final Tense[] tenses;
	private static final int tenseSize;
	private static final int personSize;
	
	/*
	 * if in topic mode (this.topic != null):
	 * 		currentWordList holds words of this topic
	 * else (in level mode):
	 * 		currentWordList holds words where difficulty == userLevel
	 */
	private List<EngSpa> currentWordList;
	/*
	 * when the user has got to the end of all the topic words
	 * we revert to levelWordList (then prompt user to choose
	 * another topic if she wants to)
	 */
	private List<EngSpa> levelWordList;
	/*
	 * words wrong at this userLevel or carried over from
	 * previous levels
	 */
	private List<EngSpa> failedWordList;
	
	private EngSpaUser engSpaUser;
	/*
	 * index to CFP_LIST
	 */
	private int cfpListIndex;
	// cache of last 3 questions asked:
	private static final int RECENTS_CT = 3;
	private EngSpa[] recentWords = new EngSpa[RECENTS_CT];
	private EngSpa currentWord;
	private QuizEventListener quizEventListener;
	private EngSpaDAO engSpaDAO;
	private char cfpChar; // C=current, F=failed, P=passed
	private int questionSequence;

	static {
		tenses = Tense.values();
		persons = Person.values();
		tenseSize = tenses.length;
		personSize = persons.length;
	}
	
	public EngSpaQuiz(EngSpaDAO engSpaDAO, EngSpaUser engSpaUser) {
		this.engSpaUser = engSpaUser;
		this.engSpaDAO = engSpaDAO;
		int userLevel = engSpaUser.getUserLevel();
		setUserLevel(userLevel);
	} 
	public void setQuizEventListener(QuizEventListener listener) {
		this.quizEventListener = listener;
	}
	public int getUserLevel() {
		return this.engSpaUser.getUserLevel();
	}
	public EngSpa getCurrentWord() {
		return this.currentWord;
	}
	/**
	 * if userLevel > maximum, based on size of dictionary,
	 * replace with USER_LEVEL_ALL.
     *
	 * Words on DB should be in difficulty order. A level is deemed to correspond
	 * to 10 words. So to get words of difficulty n, we get 10 words starting from
	 * position (n - 1) * 10.
	 * To make it more flexible, we've replaced 10 with WORDS_PER_LEVEL.
	 */
	public void setUserLevel(int level) {
		this.engSpaUser.setUserLevel(level);
		this.engSpaDAO.updateUser(engSpaUser);
		if (level == USER_LEVEL_ALL) {
			this.cfpList = ALL_LEVELS_CFP_LIST;
			this.currentWordList = null; // no currentWordList
			this.cfpListIndex = 0;
		} else {
			this.cfpList = NORMAL_CFP_LIST;
			this.currentWordList =  this.engSpaDAO.getCurrentWordList(level);
			Collections.shuffle(currentWordList);
		}
		this.failedWordList = this.engSpaDAO.getFailedWordList(engSpaUser.getUserId());
	}
	
	/**
	 * Get questions from Current, Passed and Failed lists.
	 * @return spanish
	Note: all fails kept in sync with database using userWordTable (i.e.
	fails are per user!)
	Logic:
	if no currents and no fails: endOfQuestions
	in sequence defined by CFP_LIST and cfpListIndex:
	 	if list empty, go to next list in sequence
	get Current; start from 1st
		can't be recent word
	get Failed; start from 1st
		if consecRights < 2
			can't be recent word
		else
			can't be one of previous 10 words
	get random Passed
		can't be recent word
	 */
	public String getNextQuestion2(int questionSequence) {
		this.questionSequence = questionSequence;
		if (this.currentWordList != null &&
				this.currentWordList.size() == 0 &&
				this.failedWordList.size() == 0) {
			// reached end of questions:
			if (topic == null) {
				int newUserLevel = this.engSpaDAO.validateUserLevel(
                        this.engSpaUser.getUserLevel() + 1);
				setUserLevel(newUserLevel);
				if (this.quizEventListener != null) {
					quizEventListener.onNewLevel();
				}
			}
			else {
				endOfTopic();
				if (this.quizEventListener != null) {
					quizEventListener.onTopicComplete();
				}
			}
		}
		// check each of the question types; there should be at least one available
		this.currentWord = null;
		for (int i = 0; i < cfpList.length && currentWord == null; i++) {
			this.cfpChar = cfpList[cfpListIndex];
			incrementCfpListIndex();
			if (cfpChar == 'C' && this.currentWordList.size() > 0) {
				this.currentWord = getCurrentLevelWord();
			} else if (cfpChar == 'P') {
				this.currentWord = getPassedWord();
			} else {
				this.currentWord = getNextFailedWord();
			}
		}
		if (currentWord == null) {
			// running out of words; this can only happen at level 1
			// (so no passed words) and when currentWordList is empty
			// and when words in failed list are also in recentWords
			this.cfpChar = 'F';
			this.currentWord = failedWordList.get(0);
		}

		recentWords[0] = recentWords[1];
		recentWords[1] = recentWords[2];
		recentWords[2] = currentWord;

		String spa = currentWord.getSpanish();
		String eng = currentWord.getEnglish();
		Qualifier qualifier = currentWord.getQualifier();
		WordType wordType = currentWord.getWordType();
		if (wordType == WordType.verb) {
			// choose tense based on user level:
			int verbLevel = this.engSpaUser.getUserLevel() / 5 + 1;
			if (verbLevel > tenseSize) verbLevel = tenseSize;
			Tense tense = tenses[random.nextInt(verbLevel)];
			Person person = persons[random.nextInt(personSize)];
			String spaVerb = VerbUtils.conjugateSpanishVerb(
					spa, tense, person);
			String engVerb = VerbUtils.conjugateEnglishVerb(
					eng, tense, person);
			if (tense == Tense.imperative) {
				this.spanish = spaVerb + "!";
				this.english = engVerb + "!";
			} else if (tense == Tense.noImperative) {
				this.spanish = "no " + spaVerb + "!";
				this.english = "don't " + engVerb + "!";
			} else {
				this.spanish = person.getSpaPronoun() + " " + spaVerb;
				this.english = person.getEngPronoun() + " " + engVerb;
			}
		} else if (wordType == WordType.noun) {
			if (qualifier == Qualifier.mf) {
				// randomly choose masculine or feminine:
				qualifier = random.nextBoolean()?Qualifier.masculine:Qualifier.feminine;
				if (qualifier == Qualifier.feminine && spa.endsWith("o")) {
					// replace 'o' with 'a':
					spa = spa.substring(0, spa.length() - 1) + 'a';
				}
			}
			if (random.nextBoolean()) { // definite article?
				this.english = "the " + eng;
				if (qualifier == Qualifier.feminine) {
					this.spanish = "la " + spa;
				} else {
					this.spanish = "el " + spa;
				}
			} else {
				if ("AEIOUaeiou".indexOf(eng.charAt(0)) >= 0) {
					this.english = "an " + eng;
				} else {
					this.english = "a " + eng;
				}
				if (qualifier == Qualifier.feminine) {
					this.spanish = "una " + spa;
				} else {
					this.spanish = "un " + spa;
				}
			}
		} else {
			this.spanish = spa;
			this.english = eng;
		}
		return this.spanish;
	}
	private void incrementCfpListIndex() {
		if (++this.cfpListIndex >= cfpList.length) {
			this.cfpListIndex = 0;
		}
	}
	/**
	 * @return count of number of words in currentWordList where
	 * word.isPassed() is false. Notes: currentWordList is list
	 * of words where word.difficultyLevel == userLevel;
	 * isPassed() returns true if user has correctly answered the
	 * word the required number of times.
	 */
	public int getCurrentWordCount() {
		return this.currentWordList.size();
	}
	public int getFailedWordCount() {
		return this.failedWordList.size();
	}
	public void setCorrect(boolean correct, QAStyle qaStyle) {
		boolean inFailedList = this.failedWordList.contains(currentWord);
		int consecRights = currentWord.addResult(correct, questionSequence, qaStyle);
		if (correct) {
			if (inFailedList) {
				if (consecRights > 1) {
					this.failedWordList.remove(currentWord);
				}
				if (consecRights > 2) {
					engSpaDAO.deleteUserWord(currentWord);
				} else {
					engSpaDAO.updateUserWord(currentWord);
				}
			}
			if (currentWordList != null) {
				currentWordList.remove(currentWord); // remove if in list
			}
		} else { // not correct
			if (!inFailedList) {
				currentWord.setUserId(this.engSpaUser.getUserId());
				this.failedWordList.add(currentWord);
			}
			// could be on DB.userWord but not in failed list
			// because it's removed from failed list after 2 right
			// but not from DB.userWord until 3 right
			engSpaDAO.replaceUserWord(currentWord);
		}
	}
	/**
	 * get hint from current word; make a special case
	 * for ser and estar if question is in English.
	 * @param englishQuestion true means question is English
	 * @return
	 */
	public String getHint(boolean englishQuestion) {
		String hint = this.currentWord.getHint();
		if (hint.length() == 0 && englishQuestion) {
			if (this.currentWord.getSpanish().equals("ser")) {
				hint = "permanent";
			} if (this.currentWord.getSpanish().equals("estar")) {
				hint = "temporary";
			}
		}
		return hint;
	}
	
	private EngSpa getCurrentLevelWord() {
		EngSpa es;
		for (int i = 0; i < currentWordList.size(); i++) {
			es = currentWordList.get(i);
			if (!isRecentWord(es)) {
				return es;
			}
		}
		return null;
	}
	
	/*
	 * get random word from previous level (i.e. previously got right);
	 * being random, it may be recently used, so have up to 3 attempts
	 */
	private EngSpa getPassedWord() {
		EngSpa es;
		int level = engSpaUser.getUserLevel();
		if (level < 2) return null;
		for (int i = 0; i < 3; i++) {
			es = engSpaDAO.getRandomPassedWord(level);
			if (!isRecentWord(es)) return es;
		}
		return null;
	}
	/*
	 * Return first word in failed list that was not recently used.
	 */
	private EngSpa getNextFailedWord() {
		for (EngSpa es: failedWordList) {
			if (!es.isRecentlyUsed(questionSequence) && !isRecentWord(es)) return es;
		}
		return null;
	}
	private boolean isRecentWord(EngSpa word) {
		for (int i = 0; i < RECENTS_CT; i++) {
			if (word.equals(recentWords[i])) return true;
		}
		return false;
	}
	public String getSpanish() {
		return spanish;
	}
	public String getEnglish() {
		return english;
	}
	// these 3 methods for testing purposes:
	public String getDebugState() {
		StringBuilder sb = new StringBuilder("EngSpaQuiz.currentWord=" +
				currentWord.getEnglish() + "; questionSequence=" +
				questionSequence + "; cfpChar=" + cfpChar + "; failedWordList:\n"); 
		for (EngSpa word: this.failedWordList) {
			sb.append("  " + word + "\n");
		}
		List<EngSpa> dbFailedWordList = this.engSpaDAO.getFailedWordList(engSpaUser.getUserId());
		sb.append("\ndbFailedWordList:\n");
		for (EngSpa word: dbFailedWordList) {
			sb.append("  " + word + "\n");
		}
		sb.append("\nrecentWords: ");
		for (EngSpa word: recentWords) {
			sb.append((word==null?"null":word.getEnglish()) + ", ");
		}
		return sb.toString();
	}
	public List<EngSpa> getFailedWordList() {
		return this.failedWordList;
	}
	public List<EngSpa> getCurrentWordList() {
		return this.currentWordList;
	}
	@Override // Quiz
	public int getAnswerType() {
		return Quiz.ANSWER_TYPE_STRING;
	}
	@Override // Quiz
	public String getNextQuestion(int level) throws EndOfQuestionsException {
		return "who wants to know?";
	}
	private void endOfTopic() {
		if (this.topic != null) {
			this.currentWordList = this.levelWordList;
			this.topic = null;
		}
	}
	public void setTopic(String topic) {
		if (topic == null) endOfTopic();
		else {
			this.topic = topic;
			this.levelWordList = currentWordList; // to go back to later if necessary
			this.currentWordList = engSpaDAO.findWordsByTopic(topic);
			Collections.shuffle(this.currentWordList);
		}
	}
    public String getTopic() {
        return this.topic;
    }
	/**
	 * if the current word is a failed word, return the QAStyle used when
	 * the user got it wrong; otherwise return null.
	 * @return
	 */
	public QAStyle getQAStyleFromQuestion() {
		QAStyle qaStyle = (cfpChar == 'F') ? this.currentWord.getQaStyle() : null; 
		return qaStyle;
	}

	/**
	 * Used in an emergency! Deletes all fail words.
	 */
	public void deleteAllFails() {
		this.engSpaDAO.deleteAllUserWords(-1);
		failedWordList.clear();
	}

	/**
	 * Compare 2 Spanish words for equality.
	 *
	 * @return -1 if equal, -2 if different, 0 or positive if
	 * 			differ only in accents at position n
	 */
	public static int compareSpaWords(String word1, String word2) {
		int pos;
		int res = -1;
        if (word1.length() != word2.length()) return -2;
		for (pos = 0; pos < word1.length(); pos++) {
			char char1 = word1.charAt(pos);
			char char2 = word2.charAt(pos);
			if (char1 != char2) {
				if (sameWithoutAccent(char1, char2)) {
					if (res < 0) res = pos; // only capture 1st difference
				}
				else return -2;
			}
		}
		return res;

	}
	/*
	 * Assuming char1 and char2 are different, and both in lowercase,
	 * would they be the same if their accents were removed?
	 *
	 * @return true if same with the accents removed
	 */
	private static boolean sameWithoutAccent(char char1, char char2) {
		char char1a = removeAccent(char1);
		char char2a = removeAccent(char2);
		return char1a == char2a;
	}
	/**
		UTF-8   ISO-8859-1 & cp1252
	á   C3A1    E1 (225)
	é   C3A9    E9 (233)
	í   C3AD    ED (237)
	ó   C3B3    F3 (243)
	ú   C3BA    FA (250)
	ñ   C3B1    F1 (241)

	Á   C381    C1
	É   C389    C9
	Í   C38D    CD
	Ó   C393    D3
	Ú   C39A    DA
	Ñ   C391    D1

	¡   C2A1    A1
	¿   C3BF    BF
	 */
	private static char removeAccent(char ch) {
		final char[] spaChars = {'á', 'é', 'í', 'ó', 'ú', 'ñ', 'ü'};
		final char[] engChars = {'a', 'a', 'i', 'o', 'u', 'n', 'u'};
		for (int i = 0; i < spaChars.length; i++) {
			if (ch == spaChars[i]) return engChars[i];
		}
		return ch;
	}
}
