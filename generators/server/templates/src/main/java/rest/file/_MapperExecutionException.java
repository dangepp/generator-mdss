package <%= config.packages.restFile %>;

/**
 * Generic disaese file mapping execution exception.
 */
public class MapperExecutionException extends Exception {
    public MapperExecutionException(String s) {
        super(s);
    }

    public MapperExecutionException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
