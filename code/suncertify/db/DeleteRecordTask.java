package suncertify.db;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * A database delete record operation.
 * 
 * @see AsyncFileWriterTask
 * 
 * @author Oliver Hernandez
 * 
 */
class DeleteRecordTask extends AsyncFileWriterTask {

    /**
     * Constructs a new database delete record operation.
     * 
     * @param file
     *            the database file.
     * @param offset
     *            number of bytes into the file to begin deleting a record at.
     * 
     * @see AsyncFileWriterTask#AsyncFileWriterTask(RandomAccessFile, long)
     */
    DeleteRecordTask(RandomAccessFile file, long offset) {
        super(file, offset);
    }

    /**
     * Delete a record from the database file.
     * 
     * @see AsyncFileWriterTask#execute()
     */
    @Override
    void execute() throws IOException {
        this.dbFile.seek(this.fileOffset);
        this.dbFile.writeShort(Record.DELETED);
    }

}
