package suncertify.application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Contains configuration information of the application. Configuration
 * parameters are read from and saved into a java properties file.
 * <p>
 * Implemented as a singleton.
 * 
 * @author Oliver Hernandez
 * 
 * @see Configuration
 */
public class URLyBirdConfiguration implements Configuration {

    /* the current working directory */
    private static final String CWD = System.getProperty("user.dir");

    private static final String PROPS_FILE = "suncertify.properties";

    private static final String DB_FILE_PATH = "dbfilepath";

    private static final String RMI_HOST = "rmihost";

    private static final URLyBirdConfiguration instance =
        new URLyBirdConfiguration();

    private int recordHeaderLength = 2;

    private int validRecord = 0;

    private long lockTimeout = 2000;

    private int deletedRecord = Integer.decode("0x8000");

    private Properties properties;

    private String dbFilePath;

    private String rmiHost;

    private URLyBirdConfiguration() {

        try {
            /*
             * attempt to load the saved configuration file from the current
             * working directory and read in the properties.
             */
            loadProperties();

            this.dbFilePath = findProperty(DB_FILE_PATH, "database file path");
            this.rmiHost = findProperty(RMI_HOST, "RMI server host");
        } catch (IOException e) {
            /*
             * either the properties file was not found or was invalid, so set
             * to defaults and save a new properties file.
             */

            this.properties = new Properties();
            setDBFilePath(CWD + File.separator + "db-1x2.db");
            setRMIHost("localhost");

            try {
                persist(); // save the defaults to a new properties file
            } catch (ConfigurationException ce) {
                FatalExit(ce,
                        "An error occurred saving the initial configuration");
            }
        } catch (ConfigurationException e) {
            FatalExit(e, "An error occurred reading the \"" + PROPS_FILE
                    + "\" file.");
        }

    }

    /**
     * Get the singleton instance of the <code>URLyBirdConfiguration</code>.
     * 
     * @return the singleton <code>URLyBirdConfiguration</code> object.
     */
    public static URLyBirdConfiguration getInstance() {
        return instance;
    }

    /**
     * {@inheritDoc}
     */
    public String getDBFilePath() {
        return this.dbFilePath;
    }

    /**
     * {@inheritDoc}
     */
    public void setDBFilePath(String path) {
        this.dbFilePath = path;
        this.properties.setProperty(DB_FILE_PATH, path);
    }

    /**
     * {@inheritDoc}
     */
    public int getRecordHeaderLength() {
        return this.recordHeaderLength;
    }

    /**
     * {@inheritDoc}
     */
    public void setRecordHeaderLength(int length) {
        this.recordHeaderLength = length;
    }

    /**
     * {@inheritDoc}
     */
    public int getValidRecordFlag() {
        return this.validRecord;
    }

    /**
     * {@inheritDoc}
     */
    public void setValidRecordFlag(int flag) {
        this.validRecord = flag;
    }

    /**
     * {@inheritDoc}
     */
    public int getDeletedRecordFlag() {
        return this.deletedRecord;
    }

    /**
     * {@inheritDoc}
     */
    public void setDeletedRecordFlag(int flag) {
        this.deletedRecord = flag;
    }

    /**
     * {@inheritDoc}
     */
    public long getLockTimeout() {
        return this.lockTimeout;
    }

    /**
     * {@inheritDoc}
     */
    public void setLockTimeout(long timeout) {
        this.lockTimeout = timeout;
    }

    /**
     * {@inheritDoc}
     */
    public String getRMIHost() {
        return this.rmiHost;
    }

    /**
     * {@inheritDoc}
     */
    public void setRMIHost(String host) {
        this.rmiHost = host;
        this.properties.setProperty(RMI_HOST, host);
    }

    /**
     * {@inheritDoc}
     */
    public void persist() throws ConfigurationException {
        FileOutputStream fos;

        try {
            fos = new FileOutputStream(CWD + File.separator + PROPS_FILE);
            this.properties.store(fos, "");
            fos.close();
        } catch (Exception e) {
            throw new ConfigurationException(
                    "The configuration could not be saved.", e);
        }
    }

    private void loadProperties() throws IOException {
        this.properties = new Properties();
        FileInputStream propInput = new FileInputStream(CWD + File.separator
                + PROPS_FILE);

        this.properties.load(propInput);
        propInput.close();
    }

    private String findProperty(String name, String description)
            throws ConfigurationException {
        String property;

        property = this.properties.getProperty(name);

        if (property == null) {
            throw new ConfigurationException("The " + description
                    + " was not found in the properties file.");
        }

        return property;
    }

    private void FatalExit(Exception e, String message) {
        System.err.println(message + ", system exiting.");
        e.printStackTrace();
        System.exit(-1);
    }

}
