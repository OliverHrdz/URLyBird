package suncertify.client;

/**
 * Thrown when a client cannot connect to the room reservations system via the
 * business delegate class {@link RoomReservationsDelegate}.
 * 
 * @see java.lang.Exception
 * 
 * @author Oliver Hernandez
 * 
 */
public class RoomReservationsException extends Exception {

    private static final long serialVersionUID = -1538319692230271710L;

    /**
     * Constructs a <code>RoomReservationsException</code> with no detail
     * message.
     */
    public RoomReservationsException() {
    }

    /**
     * Constructs a <code>RoomReservationsException</code> with the specified
     * detail message and cause.
     * 
     * @param message
     *            the detail message.
     * @param cause
     *            the cause of this exception.
     */
    public RoomReservationsException(String message, Throwable cause) {
        super(message, cause);
    }

}
