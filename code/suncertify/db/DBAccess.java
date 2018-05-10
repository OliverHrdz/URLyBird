package suncertify.db;

/**
 * Data access interface of the proprietary database file of the URLyBird room
 * reservation system.
 * 
 * @author Oliver Hernandez
 * 
 */
public interface DBAccess {

    /**
     * Reads a record from the file. Returns an array where each element is a
     * record value.
     * 
     * @param recNo
     *            the number of the record to read from the database.
     * @return a <code>String</code> array containing each field value from the
     *         record.
     * @throws RecordNotFoundException
     *             when the specified record is not found.
     */
    public String[] readRecord(long recNo) throws RecordNotFoundException;

    /**
     * Modifies the fields of a record. The new value for field <code>n</code>
     * appears in <code>data[n]</code>. Throws {@link SecurityException} if the
     * record is locked with a cookie other than <code>lockCookie</code>.
     * 
     * @param recNo
     *            the number of the record to update in the database.
     * @param data
     *            the array of fields to update the record with.
     * @param lockCookie
     *            the unique lock cookie (token) belonging to the single caller
     *            that can update the specified record.
     * 
     * @throws RecordNotFoundException
     *             when the specified record is not found.
     * @throws SecurityException
     *             when the record is locked with a cookie other than the
     *             specified cookie.
     */
    public void updateRecord(long recNo, String[] data, long lockCookie)
            throws RecordNotFoundException, SecurityException;

    /**
     * Deletes a record, making the record number and associated disk storage
     * available for reuse. Throws {@link SecurityException} if the record is
     * locked with a cookie other than <code>lockCookie</code>.
     * 
     * @param recNo
     *            the unique number of the record to update in the database.
     * @param lockCookie
     *            the unique lock cookie (token) belonging to the single caller
     *            that can update the specified record.
     * @throws RecordNotFoundException
     *             when the specified record is not found.
     * @throws SecurityException
     *             when the record is locked with a cookie other than the
     *             specified cookie.
     */
    public void deleteRecord(long recNo, long lockCookie)
            throws RecordNotFoundException, SecurityException;

    /**
     * Returns an array of record numbers that match the specified criteria.
     * Field <code>n</code> in the database file is described by
     * <code>criteria</code>. A <code>null</code> value in
     * <code>criteria[n]</code> matches any field value. A non-null value in
     * <code>criteria[n]</code> matches any field value that begins with
     * <code>criteria[n]</code>. (For example, "Fred" matches "Fred" or
     * "Freddy".)
     * 
     * @param criteria
     *            the search criteria to find records by.
     * @return an array of the record numbers that match the criteria.
     */
    public long[] findByCriteria(String[] criteria);

    /**
     * Creates a new record in the database (possibly reusing a deleted entry).
     * Inserts the given data, and returns the record number of the new record.
     * 
     * @param data
     *            the array of fields that make up the record to create.
     * @return the record number of the new record created.
     * @throws DuplicateKeyException
     *             when the record already exists.
     * 
     */
    public long createRecord(String[] data) throws DuplicateKeyException;

    /**
     * Locks a record so that it can only be updated or deleted by this client.
     * Returned value is a cookie that must be used when the record is unlocked,
     * updated, or deleted. If the specified record is already locked by a
     * different client, the current thread gives up the CPU and consumes no CPU
     * cycles until the record is unlocked.
     * 
     * @param recNo
     *            the number of the record to lock.
     * @return a unique cookie (token) value.
     * @throws RecordNotFoundException
     *             when the specified record is not found.
     */
    public long lockRecord(long recNo) throws RecordNotFoundException;

    /**
     * Releases the lock on a record. Cookie must be the cookie returned when
     * the record was locked; otherwise throws {@link SecurityException}.
     * 
     * @param recNo
     *            the number of the record to unlock.
     * @param cookie
     *            the unique cookie (token) value that must match the value when
     *            the record was locked.
     * @throws SecurityException
     *             when the specified cookie value does not match the value when
     *             the record was locked.
     */
    public void unlock(long recNo, long cookie) throws SecurityException;

}
