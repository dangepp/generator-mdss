package <%= config.packages.restFile %>;

/**
 * Exception thrown if a given Attribute in the file does not match any expected value.
 */
public class AttributeMissmatchException extends MapperExecutionException {
    public AttributeMissmatchException(String s) {
        super(s);
    }

    public AttributeMissmatchException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
