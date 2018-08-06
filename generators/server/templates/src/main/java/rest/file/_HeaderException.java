package <%= config.packages.restFile %>;

/**
 * Exception thrown if a given Header in the file does not match any expected value.
 */
public class HeaderException extends MapperExecutionException {
    public HeaderException(String s) {
        super(s);
    }

    public HeaderException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
