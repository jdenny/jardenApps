package evolvingwords;

import java.util.Random;

/**
 * Created by john.denny@gmail.com on 27/05/2026.
 */
public class ScrabbleLetter {
    public static final ScrabbleLetter[] LETTERS = {
            new ScrabbleLetter('A', 1),
            new ScrabbleLetter('E', 1),
            new ScrabbleLetter('I', 1),
            new ScrabbleLetter('O', 1),
            new ScrabbleLetter('U', 1),
            new ScrabbleLetter('L', 1),
            new ScrabbleLetter('N', 1),
            new ScrabbleLetter('S', 1),
            new ScrabbleLetter('T', 1),
            new ScrabbleLetter('R', 1),
            new ScrabbleLetter('D', 2),
            new ScrabbleLetter('G', 2),
            new ScrabbleLetter('B', 3),
            new ScrabbleLetter('C', 3),
            new ScrabbleLetter('M', 3),
            new ScrabbleLetter('P', 3),
            new ScrabbleLetter('F', 4),
            new ScrabbleLetter('H', 4),
            new ScrabbleLetter('V', 4),
            new ScrabbleLetter('W', 4),
            new ScrabbleLetter('Y', 4),
            new ScrabbleLetter('K', 5),
            new ScrabbleLetter('J', 8),
            new ScrabbleLetter('X', 8),
            new ScrabbleLetter('Q', 10),
            new ScrabbleLetter('Z', 10)
    };
    private char letter;
    private int value;
    private ScrabbleLetter(char letter, int value) {
        this.letter = letter;
        this.value = value;
    }
    public char getLetter() {
        return letter;
    }
    public int getValue() {
        return value;
    }
    public ScrabbleLetter getRandomLetter() {
        Random random = new Random();
        int index = random.nextInt(LETTERS.length);
        return LETTERS[index];
    }
}
