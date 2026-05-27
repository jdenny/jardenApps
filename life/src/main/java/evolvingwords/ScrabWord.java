package evolvingwords;

/**
 * Created by john.denny@gmail.com on 27/05/2026.
 */
public class ScrabWord {
    private String word;
    private int value;
    private ScrabWord parent;
    public ScrabWord(String word) {
        this.word = word;
        this.value = calculateValue();
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
    private int calculateValue() {
        int value = 0;
        for (char c : word.toCharArray()) {
            value += ScrabbleLetter.LETTERS[c - 'A'].getValue();
        }
        return value;
    }
}
