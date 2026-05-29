package evolvingwords;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by john.denny@gmail.com on 16/05/2026.
start with a few simple ScrabbleWord objects
for each ScrabbleWord:
    start a new thread
    mutate word
    if word is a Scrabble word:
        if value is >= parent.value:
            create new ScrabbleWord object, linked to parent and add it to this loop
Todo: continue to find more mutations after finding one for a particular word
    I think starting a new Thread should help?
 add lines to printTree
 Threads?
 Add score to each word of ScrabWord
Observations so far:
    harmless mutations often mutate back to the original word
    some words soon reach a dead-end - there is no single change that is a word with a higher value
 */
public class EvolveWords {
    private final static boolean debug = false;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final static char[] letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private final Set<String> wordSet = new HashSet<>();
    private static final ScrabWord adamScrab = new ScrabWord("WARM");

    public static void main(String[] args) throws IOException {
        new EvolveWords();
    }
    public EvolveWords() throws IOException {
        String fileName = // "./life/resources/docs/wordladder.txt";
                "./life/resources/docs/scrabbleWords.txt";
                // "/Users/john/AndroidStudioProjects/jardenApps/life/resources/docs/scrabbleWords.txt";
        File file = new File(fileName); // useful methods: file.getAbsolutePath(); file.isFile();
        String line;
        BufferedReader reader = new BufferedReader(new FileReader(file));
        while ((line = reader.readLine()) != null) {
            wordSet.add(line);
        }
        System.out.println("words loaded: " + wordSet.size());
        //!! run();
        executor.shutdown();
        printTree3(adamScrab, "");
    }
    public static void printTree3(ScrabWord scrabWord, String prefix) {
        System.out.println(prefix + (prefix.isEmpty() ? "" : "|_") +
                scrabWord.getWord());
        List<ScrabWord> children = scrabWord.getChildren();
        for (int i = 0; i < children.size(); i++) {
            printTree3(children.get(i), prefix + "  ");
        }
    }
    public static void printTree(ScrabWord scrabWord, String prefix) {
        System.out.println(prefix + scrabWord.getWord());
        for (ScrabWord child : scrabWord.getChildren()) {
            printTree(child, prefix + "  ");
        }
    }
    public static void printTree2(ScrabWord scrabWord, String prefix) {
        System.out.println(prefix + scrabWord.getWord());
        List<ScrabWord> children = scrabWord.getChildren();
        for (int i = 0; i < children.size(); i++) {
            printTree2(children.get(i), prefix + "  ");
        }
    }

    private class EvolveOneWord implements Runnable {
        private final ScrabWord parentScrab;
        public EvolveOneWord(ScrabWord parentScrab) {
            this.parentScrab = parentScrab;
        }

        public void run() {
            String nextWord;
            ScrabWord nextScrab;
            for (int i = 0; i < 10; i++) {
                try {
                    nextWord = findNextWord(parentScrab, 80);
                    nextScrab = new ScrabWord(nextWord);
                    parentScrab.addChild(nextScrab);
                    EvolveOneWord child = new EvolveOneWord(nextScrab);
                    executor.execute(child);
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
        }
    }
}
