package suncertify.application;

/**
 * Interface that models the application configuration. Part of the MVC design
 * of the GUI for configuring the application.
 * 
 * @author Oliver Hernandez
 * 
 */
public interface Configuration {

    /**
     * Get the full path to the database file.
     * 
     * @return path to the database file.
     */
    public String getDBFilePath();

    /**
     * Set the path to the database file.
     * 
     * @param path
     *            database file path.
     */
    public void setDBFilePath(String path);

    /**
     * Get the number of bytes in a database record header. The header contains
     * a flag indicating whether the record is valid or deleted.
     * 
     * @return the number of bytes in the record header.
     */
    public int getRecordHeaderLength();

    /**
     * Set the number of bytes in a database record header.
     * 
     * @param length
     *            the number of bytes.
     */
    public void setRecordHeaderLength(int length);

    /**
     * Get the value of the database record header that indicates a valid
     * record.
     * 
     * @return the valid record flag value.
     */
    public int getValidRecordFlag();

    /**
     * Set the value of the database record header that indicates a valid
     * record.
     * 
     * @param flag
     *            valid record flag.
     */
    public void setValidRecordFlag(int flag);

    /**
     * Get the value of the database record header the indicates a deleted
     * record.
     * 
     * @return the deleted record flag value.
     */
    public int getDeletedRecordFlag();

    /**
     * Set the value of the database record header that indicates a deleted
     * record.
     * 
     * @param flag
     *            deleted record flag.
     */
    public void setDeletedRecordFlag(int flag);

    /**
     * Get the timeout, in milliseconds, of a record lock.
     * 
     * @return the number of milliseconds before a record lock times out.
     */
    public long getLockTimeout();

    /**
     * Set the timeout, in milliseconds, of a record lock.
     * 
     * @param timeout
     *            the timeout in milliseconds.
     */
    public void setLockTimeout(long timeout);

    /**
     * Get the RMI server host name.
     * 
     * @return a host name.
     */
    public String getRMIHost();

    /**
     * Set the RMI server host name.
     * 
     * @param host
     *            the server name.
     */
    public void setRMIHost(String host);

    /**
     * Persist changes made by calls to the setters.
     * 
     * @throws ConfigurationException
     *             when an error occurs saving the configuration.
     */
    public void persist() throws ConfigurationException;

}
