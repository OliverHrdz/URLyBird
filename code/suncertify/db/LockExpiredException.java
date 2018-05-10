package suncertify.db;

/**
 * Thrown when an attempt is made to modify a record with a lock that has
 * expired.
 * 
 * @author Oliver Hernandez
 * 
 */
public class LockExpiredException extends SecurityException {

    private static final long serialVersionUID = 1457556535184434921L;

    /**
     * Constructs a <code>LockExpiredException</code> with no detail message.
     */
    public LockExpiredException() {
    }

    /**
     * Constructs a <code>LockExpiredException</code> with the specified detail
     * message.
     * 
     * @param message
     *            the detail message.
     */
    public LockExpiredException(String message) {
        super(message);
    }

}
