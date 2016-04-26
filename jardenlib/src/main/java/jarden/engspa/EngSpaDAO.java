package jarden.engspa;

import java.util.List;

public interface EngSpaDAO {
	int getDictionarySize();
	int getMaxUserLevel();

	/**
	 * Delete all existing EngSpa rows and insert new rows
	 * from contentValues. Return number of rows added.
	 */
	int newDictionary();
	int updateDictionary(List<String> updateLines);
	List<EngSpa> getCurrentWordList(int userLevel);
	EngSpa getRandomPassedWord(int userLevel);
	EngSpa getWordById(int id);
	/**
	 * get words starting with supplied Spanish
	 */
	List<EngSpa> getSpanishWord(String spanish);
	/**
	 * get words starting with supplied English
	 */
	List<EngSpa> getEnglishWord(String english);
	/**
	 * find words matching all non-null values of engSpa
	 * @param engSpa as search criteria
	 * @return
	 */
	List<EngSpa> findWords(EngSpa engSpa);
	List<EngSpa> findWordsByTopic(String topic);
	
	/**
	 * return all fails
	 */
	List<EngSpa> getFailedWordList();
	long insertUserWord(UserWord userWord);
	int updateUserWord(UserWord userWord);
	int deleteUserWord(UserWord userWord);
	/**
	 * Replace or Insert
	 * @param userWord
	 * @return
	 */
	long replaceUserWord(UserWord userWord);
	/**
	 * 
	 * @return number of rows deleted
	 */
	int deleteAllUserWords();

}
