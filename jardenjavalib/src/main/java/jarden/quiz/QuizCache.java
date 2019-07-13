package jarden.quiz;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * Cache of objects of type Quiz or list of QuizSubtypeNames.
 * 
 * Using google, e.g. "https://sites.google.com/site/amazequiz/home/problems/colores.properties"
 * 
 * either file.txt: Q: question / A: answer
 * or file.properties: question=answer
 * or file.eng: English spelling word,
 *  i.e. app speaks word in English, player types word
 * 
 * To add another maths based quiz, hard code it as per ArithmeticQuiz
 * 
 * @author john.denny@gmail.com
 * 
 */
public abstract class QuizCache {
	public final static String[] quizTypes = { "Maths", // generated questions
			// "Spanish", // questions loaded from EngSpa database on server
			"Files", // questions loaded from files on server
			"Session" // all questions we got wrong this session
	};
	private final static String[] sessionSubtypeNames = { "Failures" };
	private final static String[] mathsSubtypeNames = { "Arithmetic", "Algebra",
			"Powers", "Areas", "Series", "Fractions", "Times", "Numbers" };
	public final static String serverUrlStr =
			"https://sites.google.com/site/amazequiz/home/problems/";
	private final static String fileNamesUrlStr = 
			// "static/problem/fileList.dat";
			"fileList.txt";
	private final static String spanishNamesUrlStr =
			"spanish/supportApplet/";
	private final static String highScoresUrlStr =
			"quizmaze/setHSforApplet/?";

	private HashMap<String, Quiz> quizCacheHash;
	private URL serverUrl = null;
	private String[] fileSubtypeNames = null;
	private String[] spanishSubtypeNames = null;
	private String[] localFileNames = null;
	private String[] localSpanishSubtypeNames;
	private String[] localFileSubtypeNames;
	// if we can access our server, we will get files from there;
	// otherwise we will use the local file cache
	private boolean usingLocalCache;
	private FailuresQuiz failuresQuiz;

	public QuizCache() {
		quizCacheHash = new HashMap<String, Quiz>();
		failuresQuiz = new FailuresQuiz();
	}
	
	private URL getServerUrl() throws IOException {
		if (serverUrl == null) {
			serverUrl = new URL(serverUrlStr);
		}
		return serverUrl;
	}
	
	public String[] getSubtypeNames(String quizType) throws IOException {
		String[] subtypeNames;
		if (quizType.equals("Files")) {
			subtypeNames = this.getFileSubtypeNames();
		} else if (quizType.equals("Spanish")) {
			subtypeNames = this.getSpanishSubtypeNames();
		} else if (quizType.equals("Session")) {
			subtypeNames = QuizCache.sessionSubtypeNames;
		} else { // assume Maths by default
			subtypeNames = QuizCache.mathsSubtypeNames;
		}
		return subtypeNames;
	}
	
	private String[] getFileSubtypeNames() throws IOException {
		if (fileSubtypeNames == null) {
			try {
				fileSubtypeNames = getSubtypeNamesFromServer(fileNamesUrlStr);
				usingLocalCache = false;
			} catch(IOException ioe) {
				logCacheMessage(ioe.toString());
				loadLocalFileNames();
				fileSubtypeNames = localFileSubtypeNames;
				usingLocalCache = true;
			}
		}
		logCacheMessage("usingLocalCache: " + usingLocalCache);
		return fileSubtypeNames;
	}

	private String[] getSpanishSubtypeNames() throws IOException {
		if (spanishSubtypeNames == null) {
			try {
				spanishSubtypeNames = getSubtypeNamesFromServer(spanishNamesUrlStr);
				usingLocalCache = false;
			} catch(IOException ioe) {
				logCacheMessage(ioe.toString());
				loadLocalFileNames();
				spanishSubtypeNames = localSpanishSubtypeNames;
				usingLocalCache = true;
			}
		}
		logCacheMessage("usingLocalCache: " + usingLocalCache);
		return spanishSubtypeNames;
	}
	
	private String[] getSubtypeNamesFromServer(String namesUrlStr) throws IOException {
		URL quizListUrl = new URL(getServerUrl() + namesUrlStr);
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(quizListUrl.openStream()));
		ArrayList<String> subtypeList = new ArrayList<String>();
		String problemName;
		while ((problemName = reader.readLine()) != null) {
			if (problemName.length() > 1) {
				if (problemName.endsWith(".spa") || problemName.endsWith(".txt") ||
						problemName.endsWith(".properties")) {
					subtypeList.add(problemName);
				} else {
					// this should recognise when a proxy returns something
					// other than a list of problem types, e.g. when the server
					// is asking you to log in
					throw new IOException("unsupported quizSubtype: "
					+ problemName);
				}
			}
		}
		return subtypeList.toArray(new String[0]);
	}

	private void loadLocalFileNames() {
		if (localFileNames == null) {
			localFileNames = getLocalFileNames();
			ArrayList<String> spanishList = new ArrayList<String>();
			ArrayList<String> fileList = new ArrayList<String>();
			for (String name: localFileNames) {
				if (name.endsWith(".spa")) {
					spanishList.add(name);
				} else if (name.endsWith(".txt") || name.endsWith(".properties")){
					fileList.add(name);
				}
			}
			localSpanishSubtypeNames = spanishList.toArray(new String[0]);
			localFileSubtypeNames = fileList.toArray(new String[0]);
		}
	}

	/**
	 * We get our files from a server application written in Python. Python
	 * passes files in UTF8, whereas Java's default encoding is Cp1252 (whatever
	 * that is!). For example the spanish word estómago (stomach) is transmitted
	 * by java classes as est\u00F3mago, but by python as est\u00C3\u00B3mago
	 * which java decodes incorrectly; hence we tell Java to read the
	 * inputstream as UTF8, which decodes correctly.
	 if the object is in memory cache, return it
	 if the object can be obtained from the server
 		copy new version to local disk
 		add new version to memory cache
	 else if the object can be obtained from the local disk
	 	copy local version into cache
	 else throw exception
	 return from memory cache
	 */
	public Quiz getQuiz(String quizType, String quizSubtype) throws IOException {
		// special case, as Failures on valid for current session
		if (quizSubtype.equals("Failures")) {
			return failuresQuiz;
		}
		Quiz quiz = quizCacheHash.get(quizSubtype);
		if (quiz != null) {
			logCacheMessage(quizSubtype + " from memory cache");
			return quiz;
		}
		if (quizType.equals("Maths")) {
			quiz = getMathsSubtype(quizSubtype);
		} else {
			if (usingLocalCache) {
				quiz = loadQuizFromLocalFile(quizSubtype);
			} else {
				PresetQuiz presetQuiz = loadQuizFromServer(quizSubtype);
				copyQuizToLocalDisk(quizSubtype, presetQuiz);
				quiz = presetQuiz;
			}
		}
		quizCacheHash.put(quizSubtype, quiz);
		return quiz;
	}
	public void addFailure(QuestionAnswer qa) {
		failuresQuiz.add(qa);
	}

	public Quiz getMathsSubtype(String quizSubtype) {
		Quiz quiz;
		if (quizSubtype.equals("Algebra")) {
			quiz = new AlgebraQuiz();
		} else if (quizSubtype.equals("Areas")) {
			quiz = new AreasQuiz();
		} else if (quizSubtype.equals("Arithmetic")) {
			quiz = new ArithmeticQuiz();
		} else if (quizSubtype.equals("Fractions")) {
			quiz = new FractionsQuiz();
		} else if (quizSubtype.equals("Powers")) {
			quiz = new PowersQuiz();
		} else if (quizSubtype.equals("Series")) {
			quiz = new SeriesQuiz();
		} else if (quizSubtype.equals("Times")) {
			quiz = new TimesQuiz();
		} else if (quizSubtype.equals("Numbers")) {
			quiz = new NumbersQuiz();
		} else {
			throw new IllegalStateException("maths quizSubtype " + quizSubtype
					+ " not supported - please tell John!");
		}
		return quiz;
	}

	public abstract String[] getLocalFileNames();
	public abstract FileInputStream getFileInputStream(String fileName) throws IOException;
	public abstract FileOutputStream getFileOutputStream(String fileName) throws IOException;
	public abstract void logCacheMessage(String message);
	
	public Quiz loadQuizFromLocalFile(String quizSubtype) throws IOException {
		FileInputStream fis = getFileInputStream(quizSubtype);
		Properties properties = new Properties();
		properties.load(fis);
		Quiz quiz = new PresetQuiz(properties);
		fis.close();
		logCacheMessage(quizSubtype + " loaded from local file");
		return quiz;
	}

	public void copyQuizToLocalDisk(String fileName, PresetQuiz presetQuiz)
			throws IOException {
		Properties properties = new Properties();
		List<QuestionAnswer> qaList = presetQuiz.getQuestionAnswerList();
		String template = presetQuiz.getQuestionTemplate();
		if (template != null && template.length() > 0) {
			properties.setProperty(Quiz.TEMPLATE_KEY, template);
		}
		char questionStyle = presetQuiz.getQuestionStyle();
		char answerStyle = presetQuiz.getAnswerStyle();
		if (questionStyle != 'P' || answerStyle != 'P') {
			properties.setProperty(Quiz.IO_KEY, "" + questionStyle + answerStyle);
		}
		for (QuestionAnswer qa: qaList) {
			properties.setProperty(qa.question, qa.answer);
		}
		FileOutputStream fos = getFileOutputStream(fileName);
		// For the key, all space characters are written with a
		// preceding \ character; see java.utils.Properties API
		properties.store(fos,
				"created by AmazeQuiz; properties file for all file extensions");
		fos.close();
	}
	
	/**
	 * Load Quiz from john.jardenconsulting.com. Uses suffix of
	 * quizSubtype to determine how to get the data for the quiz:
	 * 		*.spa: get from English/Spanish database
	 * 		*.properties: get from static properties file
	 * 		*.txt: get from static text file, in form:
	 * 			Q: question
	 * 			A: answer
	 * There is some encoding to be done to get the data.
	 * Java's default encoding is taken from the environment; for
	 * johnsT500 this is Cp1252 (similar to iso-8859-1).
	 * Android's default encoding is UTF8 (although earlier releases got
	 * their encoding from the environment).
	 * We get our files from a sqlite3 Database running on the server,
	 * via an application written in Python, plus static files
	 * (both *.txt and *.properties). The files from Python
	 * are in UTF8, whereas the static files are encoded in iso-8859-1.
	 * java.util.Properties.load(InputStream) encodes in iso-8859-1,
	 * whereas Properties.load(InputStreamReader) encodes with whatever
	 * encoding is defined for the Reader, which by default - see above -
	 * is Cp1252.
	 * E.g. letter ó is:
	 * 		0x00F3 in Unicode
	 * 		0xF3 in iso-8859-1 and Cp1252
	 * 		0xC3B3 in UTF8  
	 */
	public PresetQuiz loadQuizFromServer(String quizSubtype)
			throws IOException {
		PresetQuiz quiz;
		URL problemURL;
		if (quizSubtype.endsWith(".spa")) {
			problemURL = new URL(serverUrlStr
					+ "/spanish/supportApplet/?attribute="
					+ quizSubtype.subSequence(0, quizSubtype.length() - 4));
			// UTF8 is default for Android, but not necessarily for JavaSE;
			// see javadocs above.
			InputStreamReader isr = new InputStreamReader(
					problemURL.openStream(), "UTF8");
			Properties properties = new Properties();
			properties.load(isr);
			isr.close();
			quiz = new PresetQuiz(properties);
		} else if (quizSubtype.endsWith(".txt")) {
			problemURL = new URL(serverUrlStr // + "/static/problem/"
					+ quizSubtype);
			// iso-8859 needed for Android, and maybe for Java;
			// see javadocs above
			quiz = new PresetQuiz(problemURL.openStream(), "iso-8859-1");
		} else if (quizSubtype.endsWith(".properties")) {
			problemURL = new URL(serverUrlStr // + "/static/problem/"
					+ quizSubtype);
			Properties properties = new Properties();
			// Properties.load(InputStream) uses iso-8859-1;
			// see javadocs above
			properties.load(problemURL.openStream());
			quiz = new PresetQuiz(properties);
		} else {
			throw new IllegalStateException("unsupported quizSubtype: "
					+ quizSubtype);
		}
		logCacheMessage("problemURL=" + problemURL);
		return quiz;
	}
	
	/**
	 * Send high score to server, and return all high scores for quizSubtype.
	 * @param name of player
	 * @param level reached
	 * @param quizSubtype e.g. Arithmetic
	 * @return high scores for quizSubtype
	 * @throws IOException
	 */
	public ArrayList<String> setHighScore(String name, int level,
			String quizSubtype) throws IOException {
		// at the moment we don't have a server to provide high scores!
		// so we'll just return an empty ArrayList
		boolean server = false;
		if (!server) return new ArrayList<String>();
		// TODO: provide a server somewhere!
		String params;
		if (name == null) {
			params = "problemType=" + quizSubtype;
		} else {
			params = "name=" + name + "&level=" + level + "&problemType="
					+ quizSubtype;
		}
		URL highScoreURL = new URL(getServerUrl() + highScoresUrlStr + params);
		logCacheMessage("highScoreURL=" + highScoreURL);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				highScoreURL.openStream()));
		String line;
		ArrayList<String> highScores = new ArrayList<String>();
		while ((line = reader.readLine()) != null) {
			if (line.length() > 0) {
				highScores.add(line);
			}
		}
		return highScores;
	}
}
