package suncertify.db;

/**
 * Thrown when a database record no longer exists, or is marked as deleted.
 * 
 * @author Oliver Hernandez
 * 
 */
public class RecordNotFoundException extends Exception {

    private static final long serialVersionUID = 8298038381051525181L;

    /**
     * Constructs a <code>RecordNotFoundException</code> with no detail message.
     */
    public RecordNotFoundException() {
    }

    /**
     * Constructs a <code>RecordNotFoundException</code> with the specified
     * detail message.
     * 
     * @param message
     *            the detail message.
     */
    public RecordNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a <code>RecordNotFoundException</code> with the specified
     * detail message and cause.
     * 
     * @param message
     *            the detail message.
     * @param cause
     *            the <code>Throwable</code> that caused this exception.
     */
    public RecordNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
