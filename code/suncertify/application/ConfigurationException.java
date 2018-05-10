package suncertify.application;

/**
 * Thrown when an error occurs configuring the application.
 * 
 * @author Oliver Hernandez
 * 
 */
public class ConfigurationException extends Exception {

    private static final long serialVersionUID = -6938453869785559042L;

    /**
     * Constructs a <code>ConfigurationException</code> with no detail message.
     */
    public ConfigurationException() {
    }

    /**
     * Constructs a <code>ConfigurationException</code> with the specified
     * detail message.
     * 
     * @param message
     *            the detail message.
     */
    public ConfigurationException(String message) {
        super(message);
    }

    /**
     * Constructs a <code>ConfigurationException</code> with the specified
     * detail message and cause.
     * 
     * @param message
     *            the detail message.
     * @param cause
     *            the <code>Throwable</code> that caused this exception.
     */
    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

}
