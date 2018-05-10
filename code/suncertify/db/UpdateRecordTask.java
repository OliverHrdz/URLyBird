package suncertify.db;

import java.io.IOException;
import java.io.RandomAccessFile;

import suncertify.util.StringUtil;

/**
 * A database operation to update a record in the database file.
 * 
 * @author Oliver Hernandez
 * 
 */
class UpdateRecordTask extends AsyncFileWriterTask {

    private String[] fields;

    /**
     * Constructs a new database update record operation. The record will be
     * updated with the specified <code>String</code> array of column values.
     * 
     * @param file
     *            the database file.
     * @param offset
     *            number of bytes into the file to begin updating the record at.
     * @param data
     *            the data to update the record with.
     * 
     * @see AsyncFileWriterTask#AsyncFileWriterTask(RandomAccessFile, long)
     */
    UpdateRecordTask(RandomAccessFile file, long offset, String[] data) {
        super(file, offset);
        this.fields = new String[Record.numFields];
        System.arraycopy(data, 0, this.fields, 0, Record.numFields);
    }

    void updateFields() throws IOException {

        byte[] field;
        int diff;
        byte[] padding;

        for (int currField = 0; currField < Record.numFields; currField++) {
            field = this.fields[currField].getBytes(StringUtil.DEFAULT_CHARSET);
            this.dbFile.write(field);

            /* if field value less than max field length, pad with spaces. */

            diff = Record.fieldLengths[currField] - field.length;
            if (diff > 0) {
                padding = new byte[diff];

                for (int currPad = 0; currPad < diff; currPad++) {
                    padding[currPad] = StringUtil.SPACE;
                }

                this.dbFile.write(padding);
            }
        }
    }

    /**
     * Execute the database update record operation.
     * 
     * @see AsyncFileWriterTask#AsyncFileWriterTask(RandomAccessFile, long)
     */
    void execute() throws IOException {
        this.dbFile.seek(this.fileOffset);
        updateFields();
    }
}
