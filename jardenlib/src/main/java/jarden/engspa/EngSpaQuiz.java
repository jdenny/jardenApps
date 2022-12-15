package jarden.engspa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import jarden.engspa.VerbUtils.Person;
import jarden.engspa.VerbUtils.Tense;
import jarden.provider.engspa.EngSpaContract.Topic;
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

    public enum QuizMode {
        LEARN, TOPIC, PRACTICE
    }
	public static final int WORDS_PER_LEVEL = 10;

	/*
	 * controls which list of words to choose the next question from,
	 * one of current, fails and passed
	 */
	private static final char[] LEARN_CFP_LIST = {'C', 'F', 'P', 'C', 'F'};
	private static final char[] TOPIC_CFP_LIST = {'C', 'F'};
    private static final char[] PRACTICE_CFP_LIST = {'C', 'F'};
	private char[] cfpList;
	
	private String spanish;
	private String english;
	private Person person;

	private Random random = new Random();
	private static final Person[] persons;
	private static final Tense[] tenses;
	private static final int tenseSize;
	private static final int personSize;
    private boolean modeInitialised = false;

    /*
	 * if in topic mode (this.topic != null):
	 * 		currentWordList holds words of this topic
	 * else (in learn mode):
	 * 		currentWordList holds words where difficulty == userLevel
	 */
	private List<EngSpa> currentWordList;
	/*
	 * words wrong at this userLevel or carried over from
	 * previous levels
	 */
	private List<EngSpa> failedWordList;
	
	private EngSpaUser engSpaUser;
	private int cfpListIndex;
	private static final int RECENTS_CT = 3;
	private EngSpa[] recentWords = new EngSpa[RECENTS_CT];
	private EngSpa currentWord;
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
	}
    public void setQuizMode(QuizMode quizMode) {
        this.modeInitialised = false;
        this.engSpaUser.setQuizMode(quizMode);
    }
    public void setTopic(String topic) {
        this.modeInitialised = false;
        this.engSpaUser.setTopic(topic);
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
        this.engSpaUser.setLearnLevel(level);
        this.modeInitialised = false;
    }
    public void unsetModeInitialised() {
        this.modeInitialised = false;
    }

	/**
	 * Get questions from Current, Passed and Failed lists.
	 * @return spanish
     Note: all fails kept in sync with database using userWordTable
     3

	 */
	@Override // Quiz
	public String getNextQuestion(int questionSequence) throws EndOfQuestionsException {
        if (!this.modeInitialised) resetMode();
        QuizMode quizMode = engSpaUser.getQuizMode();
        if (this.currentWordList.size() == 0 &&
                this.failedWordList.size() == 0) {
            throw new EndOfQuestionsException("quizMode=" + quizMode);
        }
        this.questionSequence = questionSequence;
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
        if (this.currentWord == null) {
            // running out of words; this can only happen at level 1
            // (so no passed words) and when any words in current or
            // failed lists are also in recentWords (e.g. in phase2)
            // so get word (not recently used) from previous and current level
            cfpChar = 'P';
            this.currentWord = getPassedWord2(this.engSpaUser.getLearnLevel() + 1);
        }
        return conjugateCurrentWord(this.currentWord);
    }
    public String conjugateCurrentWord(EngSpa currentWord) {
		recentWords[0] = recentWords[1];
		recentWords[1] = recentWords[2];
		recentWords[2] = currentWord;

		String spa = currentWord.getSpanish();
		String eng = currentWord.getEnglish();
		Qualifier qualifier = currentWord.getQualifier();
		WordType wordType = currentWord.getWordType();
		if (wordType == WordType.verb) {
			// choose tense based on user level:
			int verbLevel = this.engSpaUser.getLearnLevel() / 5 + 1;
			if (verbLevel > tenseSize) verbLevel = tenseSize;
			Tense tense = tenses[random.nextInt(verbLevel)];
			person = persons[random.nextInt(personSize)];
			String spaVerb = VerbUtils.conjugateSpanishVerb(
					spa, tense, person);
			String engVerb = VerbUtils.conjugateEnglishVerb(
					eng, tense, person);
			if (tense == Tense.imperative) {
				this.spanish = "¡" + spaVerb + "!";
				this.english = engVerb + "!";
                this.person = Person.tu;
			} else if (tense == Tense.noImperative) {
				this.spanish = "¡no " + spaVerb + "!";
				this.english = "don't " + engVerb + "!";
                this.person = Person.tu;
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
    private void resetMode() {
        QuizMode quizMode = engSpaUser.getQuizMode();
        if (quizMode == QuizMode.LEARN)  {
            initWordsForLearn();
        } else if (quizMode == QuizMode.PRACTICE)  {
            initWordsForPractice();
        } else if (quizMode == QuizMode.TOPIC) {
            initWordsForTopic();
        }
        this.modeInitialised = true;
    }
    private void initWordsForLearn() {
        int level = this.engSpaUser.getLearnLevel();
        this.cfpList = LEARN_CFP_LIST;
        this.currentWordList = this.engSpaDAO.getCurrentWordList(level);
        Collections.shuffle(currentWordList);
        this.cfpListIndex = 0;
        this.failedWordList = this.engSpaDAO.getFailedWordList();
    }
    private void initWordsForPracticeOld() {
        this.cfpList = PRACTICE_CFP_LIST;
        this.currentWordList = null; // no currentWordList
        this.cfpListIndex = 0;
        this.failedWordList = this.engSpaDAO.getFailedWordList();
    }
    private void initWordsForPractice() {
        this.cfpList = PRACTICE_CFP_LIST;
        this.currentWordList = getPassedWordSet();
        this.cfpListIndex = 0;
        this.failedWordList = this.engSpaDAO.getFailedWordList();
    }
    private void initWordsForTopic() {
        this.cfpList = TOPIC_CFP_LIST;
        this.currentWordList = engSpaDAO.findWordsByTopic(engSpaUser.getTopic());
        Collections.shuffle(this.currentWordList);
        this.cfpListIndex = 0;
        this.failedWordList = this.engSpaDAO.getFailedWordList();
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
        return (this.currentWordList == null) ? -1 : this.currentWordList.size();
	}
	public int getFailedWordCount() {
        return (this.failedWordList == null) ? -1 : this.failedWordList.size();
	}

    /**
     * @param qaStyle may be different from engSpaUser.qaStyle
     */
	public void setCorrect(boolean correct, QAStyle qaStyle) {
        if (cfpChar == 'C') {
            // we could remove this in getCurrentLevelWord()
            // but we wait until the user has attempted an answer;
            // this avoids stats of C=0; F=0 (i.e. the only thing
            // left at this level is the current question) which
            // would seem odd
            currentWordList.remove(currentWord);
        }
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
            if (currentWordList != null &&
                    !(engSpaUser.getQuizMode() == QuizMode.LEARN &&
                    engSpaUser.isLearnModePhase2() && cfpChar == 'F')) {
                // remove from currentWordList if in list, except for the case
                // where we're in phase2 of learn mode, and we've just done a failed
                // word from phase1; seems complicated, but we want the user to get
                // it right for both phases, as the question is asked differently
				currentWordList.remove(currentWord); // remove if in list
			}
		} else { // not correct
			if (!inFailedList) {
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
	 * for ser and estar if question is in English;
     * for phrases, add qualifier (e.g. 'familiar' an 'plural' for 'you' phrases).
	 * @param englishQuestion true means question is English to Spanish
	 */
	public String getHint(boolean englishQuestion) {
        Topic topic = this.currentWord.getTopic();
        String hint = (topic == Topic.n_a) ? "" : topic.toString();
        WordType wordType = this.currentWord.getWordType();
        if (englishQuestion && wordType == WordType.phrase) {
            Qualifier qualifier = this.currentWord.getQualifier();
            if (qualifier != Qualifier.n_a) {
                hint = qualifier.toString() + " " + hint;
            }
        } else if (hint.length() == 0 && (wordType != WordType.noun &&
                wordType != WordType.verb && wordType != WordType.phrase)) {
            hint = wordType.toString();
        }
        if (wordType == WordType.verb && englishQuestion) {
            if (this.currentWord.getSpanish().equals("ser")) {
                hint = "permanent " + hint;
            } if (this.currentWord.getSpanish().equals("estar")) {
                hint = "temporary " + hint;
            }
            if (this.person == Person.tu) hint = "familiar " + hint;
            else if (this.person == Person.ustedes) hint = "plural " + hint;
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

    /**
     * Get random word from below current learnLevel.
     * @return null if learnLevel 1, or return a word
     * that has not been recently used.
     */
	public EngSpa getPassedWord() {
        /*
         * get random word from previous level (i.e. previously got right);
         * being random, it may be recently used, so have up to 3 attempts
         */
        int level = engSpaUser.getLearnLevel();
        if (level < 2) return null;
        return getPassedWord2(level);
    }
    private EngSpa getPassedWord2(int level) {
        EngSpa es = engSpaDAO.getRandomPassedWord(level);
        int wordId = es.getWordId();
        int maxId = (level - 1) * WORDS_PER_LEVEL;
        for (int i = 0; isRecentWord(es) && i < RECENTS_CT; i++) {
            wordId++;
            if (wordId > maxId) wordId -= WORDS_PER_LEVEL;
            es = engSpaDAO.getWordById(wordId);
        }
        return es;
	}
	/*
     Return a set (i.e. no duplicates) of randomPassedWords, that doesn't
     include any recent words.
       add 3 recent words
       getRandomPassedWord until set contains 13;
       remove 3 recent words
	 */
	private ArrayList<EngSpa> getPassedWordSet() {
	    //
        Set<EngSpa> randomPassedSet = new HashSet<>();
        int recentsAdded = 0;
        for (int i = 0; i < RECENTS_CT; i++) {
            if (recentWords[i] != null) {
                randomPassedSet.add(recentWords[i]);
                ++recentsAdded;
            }
        }
        int level = engSpaUser.getLearnLevel();
        int targetCt = WORDS_PER_LEVEL + recentsAdded;
        do {
            randomPassedSet.add(engSpaDAO.getRandomPassedWord(level));
        } while (randomPassedSet.size() < targetCt);
        for (int i = 0; i < RECENTS_CT; i++) {
            if (recentWords[i] != null) {
                randomPassedSet.remove(recentWords[i]);
            }
        }
        return new ArrayList<>(randomPassedSet);
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
				questionSequence + "; cfpChar=" + cfpChar + "; quizMode=" +
				engSpaUser.getQuizMode() + "; phase2=" +
				engSpaUser.isLearnModePhase2() + "; qaStyle=" + engSpaUser.getQAStyle());
        if (this.failedWordList != null) {
            sb.append("; failedWordList:\n");
            for (EngSpa word: this.failedWordList) {
                sb.append("  " + word + "\n");
            }
        }
		List<EngSpa> dbFailedWordList = this.engSpaDAO.getFailedWordList();
		sb.append("dbFailedWordList:\n");
		for (EngSpa word: dbFailedWordList) {
			sb.append("  " + word + "\n");
		}
		sb.append("recentWords: ");
		for (EngSpa word: recentWords) {
			sb.append((word==null?"null":word.getEnglish()) + ", ");
		}
		return sb.toString();
	}
	@Override // Quiz
	public int getAnswerType() {
		return Quiz.ANSWER_TYPE_STRING;
	}
	/**
	 * if the current word is a failed word, return the QAStyle used when
	 * the user got it wrong; otherwise return null.
	 */
	public QAStyle getQAStyleFromQuestion() {
		return (cfpChar == 'F') ? this.currentWord.getQaStyle() : null;
	}

	/**
	 * Used in an emergency! Deletes all fail words.
	 */
	public void deleteAllFails() {
		this.engSpaDAO.deleteAllUserWords();
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
	á   C3A1    E1 (225)    option+e then vowel
	é   C3A9    E9 (233)
	í   C3AD    ED (237)
	ó   C3B3    F3 (243)
	ú   C3BA    FA (250)
	ñ   C3B1    F1 (241)    option+n then n
    ü                       uption+u then u

	Á   C381    C1
	É   C389    C9
	Í   C38D    CD
	Ó   C393    D3
	Ú   C39A    DA
	Ñ   C391    D1

	¡   C2A1    A1          option+1
	¿   C3BF    BF          option+? (doesn't work in Studio!)
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
