package evolvingwords;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by john.denny@gmail.com on 27/05/2026.
 */
public class ScrabWord {
    private static Map<Character, Integer> LETTER_VALUES = new HashMap<>();
    static {
        LETTER_VALUES.put('A', 1);
        LETTER_VALUES.put('E', 1);
        LETTER_VALUES.put('I', 1);
        LETTER_VALUES.put('O', 1);
        LETTER_VALUES.put('U', 1);
        LETTER_VALUES.put('L', 1);
        LETTER_VALUES.put('N', 1);
        LETTER_VALUES.put('S', 1);
        LETTER_VALUES.put('T', 1);
        LETTER_VALUES.put('R', 1);
        LETTER_VALUES.put('D', 2);
        LETTER_VALUES.put('G', 2);
        LETTER_VALUES.put('B', 3);
        LETTER_VALUES.put('C', 3);
        LETTER_VALUES.put('M', 3);
        LETTER_VALUES.put('P', 3);
        LETTER_VALUES.put('F', 4);
        LETTER_VALUES.put('H', 4);
        LETTER_VALUES.put('V', 4);
        LETTER_VALUES.put('W', 4);
        LETTER_VALUES.put('Y', 4);
        LETTER_VALUES.put('K', 5);
        LETTER_VALUES.put('J', 8);
        LETTER_VALUES.put('X', 8);
        LETTER_VALUES.put('Q', 10);
        LETTER_VALUES.put('Z', 10);
    }
    private String word;
    private int value;
    private ScrabWord parent;
    public ScrabWord(String word) {
        this.word = word;
        this.value = calculateValue(word);
    }
    public ScrabWord(String word, ScrabWord parent) {
        this(word);
        this.parent = parent;
    }
    public String getWord() {
        return word;
    }
    public int getValue() {
        return value;
    }
    public ScrabWord getParent() {
        return parent;
    }
    public static int calculateValue(String aWord) {
        int value = 0;
        for (char c : aWord.toCharArray()) {
            value += LETTER_VALUES.get(c);
        }
        return value;
    }
}
