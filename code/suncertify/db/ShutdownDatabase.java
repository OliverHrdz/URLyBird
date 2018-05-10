package suncertify.db;

/**
 * This thread properly handles shutting down the database when the JVM exits.
 * 
 * @see java.lang.Runtime#addShutdownHook(Thread)
 * 
 * @author Oliver Hernandez
 * 
 */
class ShutdownDatabase extends Thread {

    /**
     * Create a new shutdown thread with the specified name.
     * 
     * @param name
     *            the name of the thread.
     */
    ShutdownDatabase(String name) {
        super(name);
    }

    /**
     * Shut down the database.
     */
    @Override
    public void run() {
        Data.getInstance().close();
    }

}
