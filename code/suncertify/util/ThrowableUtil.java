package suncertify.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Utility class to retrieve stack traces from {@link java.lang.Throwable
 * Throwable} objects as a <code>String</code>.
 * 
 * @author Oliver Hernandez
 * 
 */
public class ThrowableUtil {

    /**
     * Get the specified {@link java.lang.Throwable Throwable} object's stack
     * trace as a <code>String</code>.
     * 
     * @param throwable
     *            the <code>Throwable</code> to get the stack trace from.
     * 
     * @return a <code>String</code> containing the stack trace.
     */
    public static String getStackTrace(Throwable throwable) {
        Writer result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        throwable.printStackTrace(printWriter);
        return result.toString();
    }

}
