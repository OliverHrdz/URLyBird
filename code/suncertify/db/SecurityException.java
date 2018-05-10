package suncertify.db;

/**
 * Thrown when an attempt is made to modify a database record that is locked by
 * another.
 * 
 * @author Oliver Hernandez
 * 
 */
public class SecurityException extends Exception {

    private static final long serialVersionUID = 8491334144885559308L;

    /**
     * Constructs a <code>SecurityException</code> with no detail message.
     */
    public SecurityException() {
    }

    /**
     * Constructs a <code>SecurityException</code> with the specified detail
     * message.
     * 
     * @param message
     *            the detail message.
     */
    public SecurityException(String message) {
        super(message);
    }

}
