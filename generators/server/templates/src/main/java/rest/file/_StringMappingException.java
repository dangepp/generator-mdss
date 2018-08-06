package <%= config.packages.restFile %>;

/**
 * Exception thrown if a string could not be mapped.
 */
public class StringMappingException extends MapperExecutionException {
    public StringMappingException(String s) {
        super(s);
    }

    public StringMappingException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
