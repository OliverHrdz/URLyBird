package suncertify.db;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * A database file write operation to be queued to an {@link AsyncFileWriter}
 * for later execution in a separate thread.
 * 
 * @author Oliver Hernandez
 * 
 */
abstract class AsyncFileWriterTask {

    protected RandomAccessFile dbFile;

    protected long fileOffset;

    /**
     * Constructs a new database write operation for the specified database file
     * at the specified location within the file.
     * 
     * @param file
     *            the database file.
     * @param offset
     *            number of bytes into the file to begin writing at.
     */
    AsyncFileWriterTask(RandomAccessFile file, long offset) {
        this.dbFile = file;
        this.fileOffset = offset;
    }

    /**
     * Execute the database write operation.
     * 
     * @throws IOException
     *             when an error occurs writing to the database file.
     */
    abstract void execute() throws IOException;

}
