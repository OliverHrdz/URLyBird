package suncertify.db;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import suncertify.application.URLyBirdConfiguration;
import suncertify.util.StringUtil;

/**
 * Proprietary database of the URLyBird room reservation system.
 * 
 * @author Oliver Hernandez
 * 
 */
public class Data implements DBAccess {

    static final URLyBirdConfiguration configuration = URLyBirdConfiguration
            .getInstance();

    private static final int MAGIC_COOKIE = 258;

    private static final long LOCK_TIMEOUT = configuration.getLockTimeout();

    private static final Data instance = new Data();

    private RandomAccessFile dbFile;

    private int startRecordPos;

    private long dbFileLength;

    /*
     * Records implemented as an ArrayList for quick, indexed lookups by record
     * number.
     */
    private ArrayList<Record> records;

    /*
     * Mapping of record numbers to locks. When a record is locked for
     * modification, an entry is made here.
     */
    private HashMap<Long, Lock> locks;

    /*
     * A timer to expire record lock cookies to avoid deadlocks.
     */
    private Timer lockTimer;

    /*
     * Set of expired lock cookies.
     */
    private HashSet<Long> expiredLockCookies;

    private AsyncFileWriter fileWriter;

    private Thread fileWriterThread;

    private boolean open;

    /**
     * Get the singleton instance of <code>Data</code>.
     * 
     * @return the singleton <code>Data</code> object.
     */
    public static Data getInstance() {
        return instance;
    }

    /*
     * Create an instance of the database.
     */
    private Data() {
        this.records = new ArrayList<Record>();
        this.locks = new HashMap<Long, Lock>();
        this.expiredLockCookies = new HashSet<Long>();
        this.fileWriter = new AsyncFileWriter();
        this.open = false;
    }

    /**
     * Opens the database for usage. Should only be called once during the
     * lifetime of the server.
     * 
     * @throws IOException
     *             when an error occurs opening the database.
     */
    public synchronized void open() throws IOException {
        if (!this.open) {
            String dbFilePath;
            int magicCookie;
            int currRec;
            String[] fieldValues;
            Record record;
            byte[] fieldValue;
            int fieldNameLength;
            byte[] fieldNameBytes;
            String errorMessage = "An error occurred opening the database:"
                    + StringUtil.NEW_LINE;

            dbFilePath = configuration.getDBFilePath();
            if (!(new File(dbFilePath)).exists()) {
                throw new FileNotFoundException(errorMessage + dbFilePath
                        + " (No such file or directory)");
            }

            this.dbFile = new RandomAccessFile(dbFilePath, "rws");

            try {
                magicCookie = this.dbFile.readInt();
                if (magicCookie != MAGIC_COOKIE) {
                    throw new IOException(errorMessage
                            + "Invalid database file, magic cookie "
                            + "field unknown.");
                }

                /* read in start of file and store metadata */

                this.startRecordPos = this.dbFile.readInt();
                Record.numFields = this.dbFile.readShort();

                /* read in and store schema */

                Record.fieldNames = new String[Record.numFields];
                Record.fieldLengths = new int[Record.numFields];

                for (int currField = 0; currField < Record.numFields;
                currField++) {

                    /* read in current field name */

                    fieldNameLength = this.dbFile.readShort();

                    fieldNameBytes = new byte[fieldNameLength];
                    this.dbFile.read(fieldNameBytes);
                    Record.fieldNames[currField] = StringUtil
                            .convertBytesToString(fieldNameBytes);

                    /*
                     * read in current field length
                     * and calculate record length
                     */

                    Record.fieldLengths[currField] = this.dbFile.readShort();
                    Record.recordLength += Record.fieldLengths[currField];
                }

                /* read data section */

                currRec = 0;
                fieldValues = new String[Record.numFields];

                try {
                    while (true) {

                        /* read each record in until EOF reached */

                        record = new Record();
                        record.setFilePosition(this.dbFile.getFilePointer());
                        record.setStatus(this.dbFile.readUnsignedShort());

                        /* read in each field of the current record */

                        for (int currField = 0; currField < Record.numFields;
                        currField++) {
                            fieldValue =
                                new byte[Record.fieldLengths[currField]];
                            this.dbFile.read(fieldValue);
                            fieldValues[currField] = StringUtil
                                    .convertBytesToString(fieldValue);
                        }

                        record.setRecordNumber(currRec);
                        record.setFields(fieldValues);
                        this.records.add(record);

                        currRec++;
                    }
                } catch (EOFException eof) {
                    // reached end of database file
                }

                /*
                 * create and start the background thread to asynchronously
                 * write to the database file.
                 */

                this.fileWriterThread = new Thread(this.fileWriter,
                        "AsyncFileIO");
                this.fileWriterThread.start();

                /*
                 * Create timer to release expired record locks
                 */

                this.lockTimer = new Timer("LockExpirationTimer", true);

                this.dbFileLength = this.dbFile.length();
            } catch (IOException e) {
                throw new IOException(errorMessage + e.getMessage(), e);
            }

            this.open = true;

            // register a shutdown hook that will gracefully shut down the
            // database.
            Runtime.getRuntime().addShutdownHook(
                    new ShutdownDatabase("shutdown"));
        }
    }

    /**
     * Closes the database. No further operations should be called on it.
     * Subsequent calls to this method will have no effect.
     */
    public synchronized void close() {
        if (this.open) {
            this.open = false;

            /*
             * flush out database file operations and close the file; do not
             * return from this method until asynchronous file writing is
             * completed.
             */

            this.fileWriter.add(new EndFileWritingTask(this.dbFile, 0));

            while (this.fileWriterThread.isAlive()) {
                try {
                    this.fileWriterThread.join();
                } catch (InterruptedException e) {
                    // ignore, we're just closing
                }
            }

            /*
             * Cancel the record lock expiration timer
             */

            this.lockTimer.cancel();

            /*
             * clear database metadata attributes that will be reset when the
             * open() method is called.
             */

            this.startRecordPos = 0;
            Record.resetMetadata();

            /* reset memory cache of records, along with record locking data */

            this.records = new ArrayList<Record>();
            this.locks = new HashMap<Long, Lock>();
            this.expiredLockCookies = new HashSet<Long>();

            /*
             * wake up all the threads that may be waiting on a lock; they will
             * then realize the database is closed.
             */
            notifyAll();
        }
    }

    /**
     * Deletes the specified record from the database.
     * 
     * @param recNo
     *            the unique number of the record to update in the database.
     * @param lockCookie
     *            the unique lock cookie (token) belonging to the single caller
     *            that can delete the specified record.
     * @throws RecordNotFoundException
     *             when the specified record is not found or cannot be deleted
     *             from the database file.
     * @throws SecurityException
     *             when the record is locked with a cookie other than the
     *             specified cookie.
     * 
     * @see suncertify.db.DBAccess#deleteRecord(long, long)
     */
    public synchronized void deleteRecord(long recNo, long lockCookie)
            throws RecordNotFoundException, SecurityException {

        if (this.open) {
            Record record;
            DeleteRecordTask task;

            record = getRecord(recNo);
            checkDeleted(record, recNo);

            if (isLockValid(recNo, lockCookie, true)) {
                record.setStatus(Record.DELETED);
            }

            /* queue to delete record from database file asynchronously */

            task = new DeleteRecordTask(this.dbFile, record.getFilePosition());
            this.fileWriter.add(task);
        }
    }

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
     * @return an array of the record numbers that match the criteria, or
     *         <code>null</code> if the database is closed.
     * 
     * @see suncertify.db.DBAccess#findByCriteria(java.lang.String[])
     */
    public synchronized long[] findByCriteria(String[] criteria) {
        if (this.open) {
            String[] fields;
            String field;
            String searchField;
            long[] results;
            int numNulls = 0;
            int numMatches = 0;
            int numFound = 0;
            ArrayList<Integer> foundRecords = new ArrayList<Integer>();

            /*
             * check the search criteria and count the number of null column
             * values, and column values to match the search fields by.
             */
            for (String s : criteria) {
                if (s == null) {
                    numNulls++;
                } else {
                    numMatches++;
                }
            }

            if (numNulls == Record.numFields) {
                /*
                 * search criteria passed in contained nulls for all fields, so
                 * return every valid record.
                 */
                for (int currRecord = 0; currRecord < this.records.size();
                currRecord++) {
                    if (isRecordValid(currRecord)) {
                        foundRecords.add(currRecord);
                    }
                }
            } else {

                for (int currRecord = 0; currRecord < this.records.size();
                currRecord++) {

                    if (isRecordValid(currRecord)) {
                        fields = this.records.get(currRecord).getFields();

                        /* search each field in the current record */

                        numFound = 0;

                        for (int currField = 0; currField < Record.numFields;
                        currField++) {
                            field = fields[currField];
                            searchField = criteria[currField];

                            if ((searchField != null)
                                    && (field.startsWith(searchField))) {
                                // record matches
                                numFound++;
                            }
                        }

                        /*
                         * add the current record number to the list of records
                         * found if it matches all of the search criteria
                         */
                        if (numFound == numMatches) {
                            foundRecords.add(currRecord);
                        }
                    }

                }
            }

            /* now build array of record numbers to return */

            results = new long[foundRecords.size()];

            for (int currFoundRec = 0; currFoundRec < foundRecords.size();
            currFoundRec++) {
                results[currFoundRec] = foundRecords.get(currFoundRec);
            }

            return results;
        } else {
            return null;
        }
    }

    /**
     * Locks the specified record for exclusive access to modify it. This class
     * expires locks after a timeout period specified by
     * {@link suncertify.application.Configuration#getLockTimeout()}. Therefore,
     * if a client locks a record, it should unlock it within a reasonable
     * amount of time. If it does not, such as the client crashing or an
     * exception occurring before it could unlock the record, then this class
     * will prevent the record from being locked indefinitely.
     * 
     * @param recNo
     *            the number of the record to lock.
     * 
     * @return a unique cookie (token) value
     * 
     * @throws RecordNotFoundException
     *             when the specified record is not found or the database is
     *             closed.
     * 
     * @see suncertify.db.DBAccess#lockRecord(long)
     */
    public synchronized long lockRecord(long recNo)
            throws RecordNotFoundException {

        if (this.open) {
            checkDeleted(getRecord(recNo), recNo);

            while (this.open && this.locks.containsKey(recNo)) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    // continue
                }

                /*
                 * check if another thread locked then subsequently deleted this
                 * record while this thread was waiting
                 */
                checkDeleted(getRecord(recNo), recNo);
            }

            if (this.open) {
                Lock lock = new Lock();
                this.locks.put(recNo, lock);

                /*
                 * make best effort to prevent the locker of this record from
                 * indefinitely keeping the record locked; set a task that will
                 * expire the lock after the LOCK_TIMEOUT period, but depending
                 * upon the thread scheduler, this task may not check to expire
                 * the lock until much later than the time-out period.
                 */

                this.lockTimer.schedule(new LockExpirationTask(recNo),
                        LOCK_TIMEOUT);

                return lock.getCookie();
            } else {
                throw new RecordNotFoundException("The database is closed.");
            }
        } else {
            throw new RecordNotFoundException("The database is closed.");
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @throws SecurityException
     *             when the specified cookie value does not match the value when
     *             the record was locked, or if the lock expired.
     */
    public synchronized void unlock(long recNo, long cookie)
            throws SecurityException {

        if (this.open) {
            /*
             * in this case, it doesn't matter if the lock is already unlocked,
             * so pass 'false' for the 3rd argument to islockValid(long recNo,
             * long cookie, boolean handleRecHasNoLock).
             */

            if (isLockValid(recNo, cookie, false)) {
                this.locks.remove(recNo);
                notifyAll();
            }
        }
    }

    /**
     * Read a record from the database.
     * 
     * @param recNo
     *            the number of the record to read from the database.
     * @return a <code>String</code> array containing each field value from the
     *         record.
     * @throws RecordNotFoundException
     *             when the specified record is not found or the database is
     *             closed.
     * 
     * @see suncertify.db.DBAccess#readRecord(long)
     */
    public synchronized String[] readRecord(long recNo)
            throws RecordNotFoundException {
        if (this.open) {
            Record record = getRecord(recNo);
            checkDeleted(record, recNo);
            return record.getFields();
        } else {
            throw new RecordNotFoundException("The database is closed.");
        }
    }

    /**
     * Updates the specified record with the specified field values.
     * 
     * @param recNo
     *            the number of the record to update in the database.
     * @param data
     *            the array of fields to update the record with.
     * @param cookie
     *            the unique lock cookie (token) belonging to the single caller
     *            that can update the specified record.
     * 
     * @throws RecordNotFoundException
     *             when the specified record is not found or the database is
     *             closed.
     * @throws SecurityException
     *             when the record is locked with a cookie other than the
     *             specified cookie.
     * 
     * @see suncertify.db.DBAccess#updateRecord(long, java.lang.String[], long)
     */
    public synchronized void updateRecord(long recNo, String[] data,
            long cookie) throws RecordNotFoundException, SecurityException {

        if (this.open) {
            Record record;
            UpdateRecordTask task;

            record = getRecord(recNo);

            if (isLockValid(recNo, cookie, true)) {
                record.setFields(data);

                /* queue to update record in the database file asynchronously */

                task = new UpdateRecordTask(this.dbFile, record
                        .getFilePosition() + 2, data);
                this.fileWriter.add(task);
            }
        } else {
            throw new RecordNotFoundException("The database is closed.");
        }
    }

    /**
     * Creates a new record in the database, and returns the record number where
     * the new record is created within the database.
     * 
     * @param data
     *            the array of fields that make up the new record to create.
     * @return the record number of the new record created, or -1 if the
     *         database is closed.
     * 
     * @see suncertify.db.DBAccess#createRecord(String[])
     */
    public synchronized long createRecord(String[] data)
            throws DuplicateKeyException {

        int newRecNo = -1;

        if (this.open) {
            /*
             * This implementation does not throw DuplicateKeyException as
             * specified in the interface. The reason being that the fields
             * currently present in the database are not enough to uniquely
             * identify a record, so the database would be more flexible if it
             * accepts any records for addition.
             */

            CreateRecordTask task;
            Record currRecord;
            Record newRecord = null;
            boolean creatingNewRecord = true;

            /*
             * find if we can reuse a deleted record.
             */

            for (int currRecNo = 0; currRecNo < this.records.size();
            currRecNo++) {
                currRecord = this.records.get(currRecNo);

                if (currRecord.getStatus() == Record.DELETED) {
                    creatingNewRecord = false;
                    newRecNo = currRecNo;
                    newRecord = currRecord;
                    break;
                }
            }

            if (creatingNewRecord) {
                newRecord = new Record();

                /* remember, record #s and file position are 0 based indexes */

                newRecNo = this.records.size();
                newRecord.setRecordNumber(newRecNo);
                newRecord.setFilePosition(this.dbFileLength);
                this.records.add(newRecord);

                this.dbFileLength += Record.recordLength;
            }

            /* check for and replace any null field values */

            for (int currField = 0; currField < Record.numFields; currField++) {
                if (data[currField] == null) {
                    data[currField] = " ";
                }
            }

            newRecord.setStatus(Record.VALID);
            newRecord.setFields(data);

            /* queue to create new record in database file asynchronously */

            task = new CreateRecordTask(this.dbFile, newRecord
                    .getFilePosition(), creatingNewRecord, data);
            this.fileWriter.add(task);
        }

        return newRecNo;
    }

    /**
     * Returns a formatted String containing the entire contents of the
     * database. Useful for testing purposes. If this is called after the
     * database has been closed, "The database is closed." is returned.
     * 
     * @return the database as a formatted String.
     */
    @Override
    public synchronized String toString() {
        if (this.open) {
            StringBuilder dbContents = new StringBuilder();

            dbContents.append("\t\t***** URLyBird DATABASE *****\n\n\n");

            dbContents.append("Magic Cookie = ");
            dbContents.append(MAGIC_COOKIE + "\n");
            dbContents.append("Offset To First Record = ");
            dbContents.append(this.startRecordPos + "\n");
            dbContents.append("Number Of Fields Per Record = ");
            dbContents.append(Record.numFields + "\n\n");

            dbContents.append("  Data Records:\n\n");

            int currRec = 0;

            for (Record record : this.records) {
                dbContents.append("Record #" + currRec + " Data:\n");
                dbContents.append(record.toString());
                currRec++;
            }

            return dbContents.toString();
        } else {
            return "The database is closed.";
        }
    }

    /*
     * Helper method to uniformly retrieve a record.
     */
    private Record getRecord(long recNo) throws RecordNotFoundException {
        Record record = null;

        try {
            record = this.records.get((int) recNo);
        } catch (IndexOutOfBoundsException e) {
            throw new RecordNotFoundException("Record #" + recNo
                    + " does not exist in the database.");
        }

        if (record == null) {
            /*
             * throw an exception when the internal ArrayList returns null at
             * the specified index
             */

            throw new RecordNotFoundException("Record #" + recNo
                    + " not found in the database.");
        }

        return record;
    }

    /*
     * Helper method to uniformly check whether or not a record is deleted.
     */
    private void checkDeleted(Record record, long recNo)
            throws RecordNotFoundException {
        if (record.getStatus() == Record.DELETED) {
            throw new RecordNotFoundException("Record #" + recNo
                    + " has been deleted from the database.");
        }
    }

    /*
     * Helper method to determine if a record is valid or not.
     */
    private boolean isRecordValid(long recNo) {
        boolean valid = false;

        try {
            Record r = getRecord(recNo);

            if (r.getStatus() == Record.VALID) {
                valid = true;
            }
        } catch (RecordNotFoundException e) {
            // treat as record is not valid
        }

        return valid;
    }

    /*
     * Helper method to determine if a cookie value belongs to a valid lock on
     * the specified record.
     */
    private boolean isLockValid(long recNo, long lockCookie,
            boolean handleRecHasNoLock) throws SecurityException {

        if (this.expiredLockCookies.contains(lockCookie)) {
            throw new LockExpiredException("Client's record lock expired.");
        }

        boolean valid = false;
        Lock lock = this.locks.get(recNo);

        if (lock != null) {
            if (lock.getCookie() == lockCookie) {
                valid = true;
            } else {
                throw new SecurityException("Record not locked by "
                        + "this client.");
            }
        } else {
            if (handleRecHasNoLock) {
                throw new SecurityException("Record not locked by "
                        + "this client.");
            }
        }

        return valid;
    }

    /*
     * A java.util.TimerTask that will unlock a record lock that is held longer
     * than the configured time-out period. Due to various thread scheduling
     * implementations, this mechanism makes a "best attempt" at preventing a
     * client from indefinitely keeping a record locked. The reason being that
     * the thread that will execute this task may not get CPU time until much
     * later than the determined time-out period.
     */
    private class LockExpirationTask extends TimerTask {

        private long recordNumber;

        LockExpirationTask(long recNo) {
            this.recordNumber = recNo;
        }

        @Override
        public void run() {

            Data data = Data.this;

            synchronized (data) {

                if (data.open) {
                    Lock lock = data.locks.get(this.recordNumber);

                    /*
                     * only expire the lock if the client currently holding the
                     * lock has not yet unlocked the record.
                     */

                    if (lock != null) {
                        data.expiredLockCookies.add(lock.getCookie());
                        data.locks.remove(this.recordNumber);
                        data.notifyAll();
                    }
                }
            }

        }

    }

}
