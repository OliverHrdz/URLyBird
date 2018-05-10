package suncertify.db;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Marker class to indicate to the {@link AsyncFileWriter} to stop executing
 * database file operations. Will close the database file.
 * 
 * @author Oliver Hernandez
 * 
 */
class EndFileWritingTask extends AsyncFileWriterTask {

    /**
     * Constructs a new <code>EndFileWritingTask</code> to signal the database
     * will be closed. Does not write to the database file.
     * 
     * @param file
     *            the database file to close.
     * @param offset
     *            a dummy offset.
     */
    EndFileWritingTask(RandomAccessFile file, long offset) {
        super(file, offset);
    }

    /**
     * Closes the database file this task was constructed with.
     * 
     * @see AsyncFileWriterTask#execute()
     */
    @Override
    void execute() throws IOException {
        this.dbFile.close();
    }

}
