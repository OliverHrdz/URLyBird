package suncertify.db;

/**
 * Thrown when an attempt is made to insert a record that already exists in the
 * database.
 * 
 * @author Oliver Hernandez
 * 
 */
public class DuplicateKeyException extends Exception {

    private static final long serialVersionUID = -6689165809485807888L;

    /**
     * Constructs a <code>DuplicateKeyException</code> with no detail message.
     */
    public DuplicateKeyException() {
    }

    /**
     * Constructs a <code>DuplicateKeyException</code> with the specified detail
     * message.
     * 
     * @param message
     *            the detail message.
     */
    public DuplicateKeyException(String message) {
        super(message);
    }

}
