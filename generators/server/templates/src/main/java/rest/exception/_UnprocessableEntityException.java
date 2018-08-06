package <%= config.packages.restException %>;

/**
 * REST Exception thrown if a given entity could not processed.
 */
public class UnprocessableEntityException extends RuntimeException {
    public UnprocessableEntityException(String s) {
        super(s);
    }

    public UnprocessableEntityException(String s, Throwable throwable) {
        super(createMessage(s, throwable), throwable);
    }

    private static String createMessage(String s, Throwable throwable) {
        StringBuilder message = new StringBuilder(s);
        Throwable t = throwable;
        while (t != null) {
            message.append("; ").append(t.getMessage());
            t = t.getCause();
        }
        return message.toString();
    }
}
