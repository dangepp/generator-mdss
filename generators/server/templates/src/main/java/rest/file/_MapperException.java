package <%= config.packages.restFile %>;

/**
 * Generic disaese file mapper exception.
 */
public class MapperException extends Exception {
    public MapperException(String s) {
        super(s);
    }

    public MapperException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
