package suncertify.application;

/**
 * The execution modes of the application.
 * 
 * @author Oliver Hernandez
 * 
 */
public enum ExecutionMode {
    CLIENT(""), SERVER("server"), STANDALONE("alone");

    private final String execMode;

    /**
     * Constructs an enumeration instance for the <code>String</code>
     * representation of the execution mode.
     * 
     * @param mode
     *            an execution mode.
     */
    ExecutionMode(String mode) {
        this.execMode = mode;
    }

    /**
     * Retrieves the enumeration constant corresponding to the execution mode.
     * 
     * @param mode
     *            a <code>String</code> containing an execution mode value.
     * @return an <code>ExecutionMode</code> for the value passed in, or
     *         <code>null</code> if no enum exists for the mode value.
     */
    public static ExecutionMode getMode(String mode) {
        for (ExecutionMode em : ExecutionMode.values()) {
            switch (em) {
            case CLIENT:
                if (CLIENT.toString().equals(mode.toLowerCase())) {
                    return em;
                }
                break;
            case SERVER:
                if (SERVER.toString().equals(mode.toLowerCase())) {
                    return em;
                }
                break;
            case STANDALONE:
                if (STANDALONE.toString().equals(mode.toLowerCase())) {
                    return em;
                }
            }
        }

        return null;
    }

    /**
     * Get the <code>String</code> value of the execution mode.
     * 
     * @return the execution mode.
     */
    @Override
    public String toString() {
        return this.execMode;
    }

}
