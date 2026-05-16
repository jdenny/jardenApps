package evolvingwords;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by john.denny@gmail.com on 16/05/2026.
 */
public class EvolveWords {
    private String[] wordArrary = {
            "activity",
            "actor",
            "actress",
            "add",
            "address",
            "adult",
            "and",
            "id"
    };
    private Set<String> wordSet = new HashSet<>();

    public static void main(String[] args) {
        new EvolveWords().run();
    }
    public EvolveWords() {
        for (String word : wordArrary) {
            wordSet.add(word);
        }
    }
    private void run() {
        String firstWord = "aid";
        for (int i = 0; i < 10; i++) {
            String secondWord = mutate(firstWord);
            if (isWord(secondWord)) {
                System.out.println("***********mutated is a word! " + secondWord);
            } else {
                System.out.println("mutated is not a word! " + secondWord);
            }
        }
    }
    private String mutate(String word) {
        String mutated;
        Random random = new Random();
        int a = random.nextInt(3);
        if (a == 0) { // add letter
            int b = random.nextInt(word.length()+ 1);
            char c = getRandomLetter();
            mutated = word.substring(0, b) + c + word.substring(b);
            System.out.println("added " + c + " to position " + b);
        } else if (a == 1) { // remove letter
            int b = random.nextInt(word.length());
            mutated = word.substring(0, b) + word.substring(b + 1);
            System.out.println("removed letter at position " + b);
        } else { // replace letter
            int b = random.nextInt(word.length());
            char c = getRandomLetter();
            mutated = word.substring(0, b) + c + word.substring(b + 1);
            System.out.println("replaced letter at position " + b + " with " + c);
        }
        return mutated;
    }
    private char getRandomLetter() {
        final char[] letters = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        Random random = new Random();
        int index = random.nextInt(letters.length);
        return letters[index];
    }
    private boolean isWord(String word) {
        return wordSet.contains(word);
    }
}
