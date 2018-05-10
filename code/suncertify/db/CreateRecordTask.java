package suncertify.db;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * A database operation to create a new record.
 * 
 * @author Oliver Hernandez
 * 
 */
class CreateRecordTask extends UpdateRecordTask {

    private boolean creatingNewRecord;

    /**
     * Constructs a new database create record operation, specifying if a
     * deleted record will be reused or a new record appended.
     * 
     * @see AsyncFileWriterTask#AsyncFileWriterTask(RandomAccessFile, long)
     * 
     * @param file
     *            the database file.
     * @param offset
     *            number of bytes into the file to begin writing new record at.
     * @param creatingNew
     *            <code>true</code> if a new record will be appended,
     *            <code>false</code> otherwise.
     * @param data
     *            the new record to create.
     */
    CreateRecordTask(RandomAccessFile file, long offset, boolean creatingNew,
            String[] data) {

        super(file, offset, data);
        this.creatingNewRecord = creatingNew;
    }

    /**
     * Create the new record in the database.
     * 
     * @throws IOException
     *             when an error occurs writing to the database file.
     */
    void execute() throws IOException {

        this.dbFile.seek(this.fileOffset);
        this.dbFile.writeShort(Record.VALID);

        super.updateFields();

        /*
         * set the database file length if a new 
         * record was appended to the file
         */
        if (this.creatingNewRecord) {
            this.dbFile.setLength(this.fileOffset + Record.recordLength);
        }
    }

}
