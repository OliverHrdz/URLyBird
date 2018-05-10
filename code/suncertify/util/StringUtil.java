package suncertify.util;

import java.io.UnsupportedEncodingException;

/**
 * String utilities.
 * 
 * @author Oliver Hernandez
 * 
 */
public class StringUtil {

    /**
     * The default character set encoding, currently <code>US-ASCII</code>.
     */
    public static final String DEFAULT_CHARSET = "US-ASCII";

    /**
     * The system dependent newline character.
     */
    public static final String NEW_LINE = System.getProperty("line.separator");

    /**
     * The byte value of the space character in the default encoding specified
     * by {@link #DEFAULT_CHARSET}. Currently <code>US-ASCII</code>.
     */
    public static final byte SPACE = 0x20;

    /**
     * Converts bytes of <code>ASCII</code> characters to a Java
     * <code>String</code>.
     * 
     * @return the array of character bytes as a Java String object.
     * 
     * @throws UnsupportedEncodingException
     *             when the bytes cannot be converted to a <code>String</code>.
     */
    public static String convertBytesToString(byte[] bytes)
            throws UnsupportedEncodingException {

        String str = "";

        str = new String(bytes, DEFAULT_CHARSET);

        return str;
    }

}
