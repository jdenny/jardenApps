package evolvingwords;

/**
 * Created by john.denny@gmail.com on 28/05/2026.
 */
public class NoSuitableWordFoundException extends Throwable {
    private static final long serialVersionUID = 1L;

    public NoSuitableWordFoundException() {}
    public NoSuitableWordFoundException(String message) {
        super(message);
    }
}
