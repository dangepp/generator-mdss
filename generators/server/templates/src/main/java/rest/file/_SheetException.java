package <%= config.packages.restFile %>;

/**
 * Exception thrown if a sheet of a .xls file could not be mapped.
 */
public class SheetException extends MapperExecutionException {
    public SheetException(String s) {
        super(s);
    }

    public SheetException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
