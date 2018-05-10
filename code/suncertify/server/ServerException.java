package suncertify.server;

/**
 * Thrown when an error occurs in the room reservations system server.
 * 
 * @author Oliver Hernandez
 * 
 */
public class ServerException extends Exception {

    private static final long serialVersionUID = 6960054610080551064L;

    /**
     * Constructs a <code>ServerException</code> with no detail message.
     */
    public ServerException() {
    }

    /**
     * Constructs a <code>ServerException</code> with the specified detail
     * message.
     * 
     * @param message
     *            the detail message.
     */
    public ServerException(String message) {
        super(message);
    }

    /**
     * Constructs a <code>ServerException</code> with the specified detail
     * message and cause.
     * 
     * @param message
     *            the detail message.
     * @param cause
     *            the <code>Throwable</code> that caused this exception.
     */
    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }

}
