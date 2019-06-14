package jarden.cards;

/**
 * Created by john.denny@gmail.com on 2019-06-14.
 */
public class BadBridgeTokenException extends IllegalArgumentException {
    public BadBridgeTokenException(String answer, IllegalArgumentException nfe) {
        super(answer, nfe);
    }
}
