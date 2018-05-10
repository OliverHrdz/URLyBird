package suncertify.db;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.security.SecureRandom;

/**
 * An abstraction of a lock on a database record.
 * 
 * @author Oliver Hernandez
 * 
 */
class Lock {

    private static final SecureRandom SRNG = new SecureRandom();

    private Long lock;

    /**
     * Create a <code>Lock</code> object that will contain a unique 64-bit
     * number that can be used by clients that obtain this lock to identify
     * themselves.
     */
    Lock() {
        DataInputStream dataIn;
        byte[] bytes = new byte[8]; // 8 bytes to hold a 64 bit long

        SRNG.nextBytes(bytes); // generate 8 random bytes
        dataIn = new DataInputStream(new ByteArrayInputStream(bytes));

        try {
            this.lock = dataIn.readLong(); // read the 8 bytes as a long
        } catch (IOException e) {
            // should never happen as IO stream is from memory
            e.printStackTrace();
        }
    }

    /**
     * Get the unique cookie value of this <code>Lock</code>.
     * 
     * @return a unique long value.
     */
    long getCookie() {
        return this.lock;
    }

}
