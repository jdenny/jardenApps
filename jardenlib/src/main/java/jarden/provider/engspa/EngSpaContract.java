package jarden.provider.engspa;

import android.net.Uri;
import android.provider.BaseColumns;

public final class EngSpaContract {
	public static final String AUTHORITY = "com.jardenconsulting.engspa.provider";
	
	// constants for EngSpa table:
	public static final String TABLE = "EngSpa";
	public static final String CONTENT_URI_STR = "content://" + AUTHORITY + "/" + TABLE;
	public static final Uri CONTENT_URI_ENGSPA = Uri.parse(CONTENT_URI_STR);
	public static final String ENGLISH = "english";
	public static final String SPANISH = "spanish";
	public static final String WORD_TYPE = "wordType";
	public static final String QUALIFIER = "qualifier";
	public static final String ATTRIBUTE = "attribute";
	public static final String LEVEL = "level";
	public static final String[] PROJECTION_ALL_FIELDS = {
		BaseColumns._ID, ENGLISH, SPANISH, WORD_TYPE, QUALIFIER,
		ATTRIBUTE, LEVEL
	};
	
	// constants for User table:
	public static final String USER_TABLE = "User";
	public static final String CONTENT_URI_USER_STR = "content://" +
			AUTHORITY  + "/" + USER_TABLE;
	public static final Uri CONTENT_URI_USER = Uri.parse(CONTENT_URI_USER_STR);
	public static final String NAME = "name";
	// TODO: change literal to "qaStyle" next time we update database
	public static final String QA_STYLE = "questionStyle";
	public static final String[] PROJECTION_ALL_USER_FIELDS = {
		BaseColumns._ID, NAME, LEVEL, QA_STYLE
	};

	// constants for UserWord table:
	public static final String USER_WORD_TABLE = "UserWord";
	public static final String CONTENT_URI_USER_WORD_STR = "content://" +
			AUTHORITY  + "/" + USER_WORD_TABLE;
	public static final Uri CONTENT_URI_USER_WORD =
			Uri.parse(CONTENT_URI_USER_WORD_STR);
	public static final String USER_ID = "userId";
	public static final String WORD_ID = "wordId";
	public static final String CONSEC_RIGHT_CT = "consecutiveRightCt";
	public static final String QUESTION_SEQUENCE = "questionSequence";
	public static final String[] PROJECTION_ALL_USER_WORD_FIELDS = {
		USER_ID, WORD_ID, CONSEC_RIGHT_CT, QUESTION_SEQUENCE, QA_STYLE
	};
	public static final String[] PROJECTION_ALL_FAILED_WORD_FIELDS = {
		BaseColumns._ID, ENGLISH, SPANISH, WORD_TYPE, QUALIFIER,
		ATTRIBUTE, LEVEL,
		CONSEC_RIGHT_CT, QUESTION_SEQUENCE, QA_STYLE
	};
	public static final String FAILED_WORD_VIEW = "FailedWordView";
	
	public enum VoiceText {
		voice, text, both;
	}
	public enum QAStyle {
        spokenWrittenSpaToEng("1. Spoken & Written Spa→Eng", VoiceText.both, true, false),
        writtenEngToSpa("2. Written Eng→Spa", VoiceText.text, false, true),
        spokenSpaToEng("3. Spoken Spa→Eng", VoiceText.voice, true, false),
        writtenSpaToEng("4. Written Spa→Eng", VoiceText.text, true, false),
        spokenSpaToSpa("5. Spoken Spa→Spa", VoiceText.voice, true, true),
        random("Random 1 to 5", null, false, false),
        alternate("Alternate 2 & 3", null, false, false);

        public final String fullName;
        public final VoiceText voiceText;
		public final boolean spaQuestion;
		public final boolean spaAnswer;

		QAStyle(String fullName, VoiceText voiceText, boolean spaQ, boolean spaA) {
            this.fullName = fullName;
			this.voiceText = voiceText;
			this.spaQuestion = spaQ;
			this.spaAnswer = spaA;
		}
	}
	
	public enum WordType {
		noun, verb, adjective, adverb, number,
		pronoun, preposition, conjunction, phrase
	}
	
	public enum Qualifier {
		n_a, // not applicable
		masculine, feminine, // for nouns
		transitive, intransitive, transIntrans, auxiliary, // for verbs
		// added 16.2.2016:
		mf, mpl, fpl, mfpl; // for nouns; masculine or feminine, then plurals
	}
	
	public enum Attribute {
		animal, body, building, clothing, colour, culture, drink, food,
		hobby, home, interrogative, language, mineral, money, music, n_a,
		person, place, size, sport, technology, time,
		travel, weather
	}
	public static final String[] wordTypeNames;
	public static final String[] qualifierNames;
	public static final String[] attributeNames;
	public static final String[] qaStyleNames;
	
	static {
		WordType[] wordTypes = WordType.values();
		wordTypeNames = new String[wordTypes.length];
		for (int i = 0; i < wordTypes.length; i++) {
			wordTypeNames[i] = wordTypes[i].name();
		}
		Qualifier[] qualifiers = Qualifier.values();
		qualifierNames = new String[qualifiers.length];
		for (int i = 0; i < qualifiers.length; i++) {
			qualifierNames[i] = qualifiers[i].name();
		}
		Attribute[] attributes = Attribute.values();
		attributeNames = new String[attributes.length];
		for (int i = 0; i < attributes.length; i++) {
			attributeNames[i] = attributes[i].name();
		}
		QAStyle[] qaStyles = QAStyle.values();
		qaStyleNames = new String[qaStyles.length];
		for (int i = 0; i < qaStyles.length; i++) {
			qaStyleNames[i] = qaStyles[i].fullName;
		}

	}
}
