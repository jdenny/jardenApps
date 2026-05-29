package evolvingwords;

import java.util.Random;
import java.util.Set;

/**
 * Created by john.denny@gmail.com on 29/05/2026.
 */
public class EvolveOneWord implements Runnable {
    private final static boolean debug = false;
    private final static char[] letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private final Set<String> wordSet;
    private final ScrabWord parentScrab;

    public EvolveOneWord(ScrabWord parentScrab, Set<String> wordSet) {
        this.wordSet = wordSet;
        this.parentScrab = parentScrab;
    }
    @Override
    public void run() {
        String nextWord;
        ScrabWord nextScrab;
        for (int i = 0; i < 10; i++) {
            try {
                nextWord = findNextWord(parentScrab, 80);
                nextScrab = new ScrabWord(nextWord);
                parentScrab.addChild(nextScrab);
                EvolveOneWord child = new EvolveOneWord(nextScrab, wordSet);
                //!!?? executor.execute(child);
                //!! parentScrab = nextScrab;
            } catch (NoSuitableWordFoundException e) {
                System.out.println("no suitable word found");
                return;
            }
        }

    }
    private String findNextWord(ScrabWord parentScrab, int limit)
            throws NoSuitableWordFoundException {
        String nextWord;
        String parentWord = parentScrab.getWord();
        int parentValue = parentScrab.getValue();
        for (int i = 1; i <= limit; i++) {
            nextWord = mutate(parentWord);
            if (isWord(nextWord)) {
                System.out.println(nextWord + " found after " + i + " mutations");
                int value = ScrabWord.calculateValue(nextWord);
                if (value >= parentValue) {
                    System.out.println(nextWord + " has value " + value +
                            " >= parent value " + parentValue);
                    return nextWord;
                }
            }
        }
        throw new NoSuitableWordFoundException();
    }
    private String mutate(String word) {
        String mutated;
        Random random = new Random();
        int a = random.nextInt(3);
        if (a == 0) { // add letter
            int b = random.nextInt(word.length() + 1);
            char c = getRandomLetter();
            mutated = word.substring(0, b) + c + word.substring(b);
            if (debug) System.out.println("added " + c + " to position " + b);
        } else if (a == 1) { // remove letter
            int b = random.nextInt(word.length());
            mutated = word.substring(0, b) + word.substring(b + 1);
            if (debug) System.out.println("removed letter at position " + b);
        } else { // replace letter
            int b = random.nextInt(word.length());
            char c = getRandomLetter(word.charAt(b));
            mutated = word.substring(0, b) + c + word.substring(b + 1);
            if (debug) System.out.println("replaced letter at position " + b + " with " + c);
        }
        if (debug) System.out.println("mutated word: " + mutated);
        return mutated;
    }
    private char getRandomLetter() {
        Random random = new Random();
        int index = random.nextInt(letters.length);
        return letters[index];
    }
    private char getRandomLetter(char except) {
        Random random = new Random();
        int index = random.nextInt(letters.length);
        char c = letters[index];
        if (c == except) {
            if (++index >= letters.length) index = 0;
            c = letters[index];
        }
        return c;
    }
    private boolean isWord(String word) {
        return wordSet.contains(word);
    }}
