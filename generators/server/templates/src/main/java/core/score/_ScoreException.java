package <%= config.packages.score %>;

/**
 * Exception for Score classes
 */
public class ScoreException extends Exception {
    public ScoreException(String msg0) {
        super(msg0);
    }

    public ScoreException(String msg, Exception e) {
        super(msg, e);
    }
}
