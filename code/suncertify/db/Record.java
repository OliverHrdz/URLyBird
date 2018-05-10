package suncertify.db;

/**
 * A record from the database.
 * 
 * @author Oliver Hernandez
 * 
 */
class Record {

    private static final int RECORD_HEADER_LENGTH = Data.configuration
            .getRecordHeaderLength();

    /**
     * Flag indicating a record is valid.
     */
    static final int VALID = Data.configuration.getValidRecordFlag();

    /**
     * Flag indicating a record is deleted.
     */
    static final int DELETED = Data.configuration.getDeletedRecordFlag();

    static int recordLength = RECORD_HEADER_LENGTH;

    static int numFields;

    static String[] fieldNames;

    static int[] fieldLengths;

    private long filePosition;

    private int recordNumber;

    private int status;

    private String[] fields;

    /**
     * Constructs a new <code>Record</code> object.
     */
    Record() {
    }

    /**
     * Reset record metadata.
     */
    static void resetMetadata() {
        numFields = 0;
        fieldNames = null;
        fieldLengths = null;
        recordLength = RECORD_HEADER_LENGTH;
    }

    /**
     * Retrieve the record status, whether it is valid or deleted.
     * 
     * @return either {@link #VALID} or {@link #DELETED}.
     */
    int getStatus() {
        return this.status;
    }

    /**
     * Set the status of the record as either valid or deleted.
     * 
     * @param status
     *            the status to set the record to, either {@link #VALID} or
     *            {@link #DELETED}.
     */
    void setStatus(int status) {
        this.status = status;
    }

    /**
     * Get the record's starting position in the database file.
     * 
     * @return the offset into the database file where this record starts.
     */
    long getFilePosition() {
        return this.filePosition;
    }

    /**
     * Set the record's starting position in the database file.
     * 
     * @param filePos
     *            the offset into the database file to set where this record
     *            starts.
     */
    void setFilePosition(long filePos) {
        this.filePosition = filePos;
    }

    /**
     * Get the record's number, which is it's location in the database.
     * 
     * @return the record number.
     */
    int getRecordNumber() {
        return this.recordNumber;
    }

    /**
     * Set the record's number, which is it's location in the database.
     * 
     * @param recNo
     *            the database location to set the record number to.
     */
    void setRecordNumber(int recNo) {
        this.recordNumber = recNo;
    }

    /**
     * Returns the fields in this record. A copy of the internal array is
     * returned to prevent any side effects with the returned array being
     * modified in the calling environment.
     * 
     * @return a <code>String</code> array containing the values of the fields
     *         in this record.
     */
    String[] getFields() {
        String[] copyFields = new String[numFields];
        System.arraycopy(this.fields, 0, copyFields, 0, numFields);

        return copyFields;
    }

    /**
     * Update this record's fields.
     * 
     * @param newFields
     *            a <code>String</code> array containing values to update all of
     *            the fields with.
     */
    void setFields(String[] newFields) {
        this.fields = new String[numFields];
        System.arraycopy(newFields, 0, this.fields, 0, numFields);
    }

    /**
     * Returns a formatted String containing the contents of this record.
     * 
     * @return the record as a formatted String.
     */
    @Override
    public String toString() {
        String fieldValue;
        StringBuilder contents = new StringBuilder();

        contents.append("\tRecord File Position = " + this.filePosition + "\n");
        contents.append("\tRecord Number = " + this.recordNumber + "\n");
        contents.append("\tStatus = " + this.status + "\n");

        if (this.fields.length != numFields) {
            /* this should never occur */
            System.err.println("Database Corrupted, Aborting!");
            System.exit(-1);
        }

        for (int currField = 0; currField < numFields; currField++) {
            fieldValue = this.fields[currField];
            contents.append("\tField '" + fieldNames[currField] + "' = '"
                    + fieldValue + "'\n");
        }

        return contents.toString();
    }

}
