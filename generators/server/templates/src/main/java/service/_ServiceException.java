package <%= config.packages.service %>;

/**
 * Exception thrown by Service methods
 */
public class ServiceException extends Exception {
    public ServiceException(String msg0) {
        super(msg0);
    }

    public ServiceException(String msg, Exception e) {
        super(msg, e);
    }
}
