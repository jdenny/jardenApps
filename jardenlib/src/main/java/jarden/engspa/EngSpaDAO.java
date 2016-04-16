package jarden.engspa;

import java.util.List;

public interface EngSpaDAO {
	int getDictionarySize();
	int getMaxUserLevel();
    int validateUserLevel(int userLevel);

	EngSpaUser getUser();
	long insertUser(EngSpaUser engSpaUser);
	int updateUser(EngSpaUser engSpaUser);
    int updateUserLevel(int level);
	int deleteUser(EngSpaUser engSpaUser);
	
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
	 * if userId < 1: return all fails
	 * else: return fails for specified user
	 * @param userId
	 * @return
	 */
	List<EngSpa> getFailedWordList(int userId);
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
	 * @param userId if < 1 then delete all user words
	 *  else delete all users words for specified user.
	 * @return number of rows deleted
	 */
	int deleteAllUserWords(int userId);

}
