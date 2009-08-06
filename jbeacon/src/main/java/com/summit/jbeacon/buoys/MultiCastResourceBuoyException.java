/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.summit.jbeacon.buoys;

/**
 *
 * @author justin
 */
public class MultiCastResourceBuoyException extends Exception {

    /**
     * Constructor to set msg, start a new stack trace.
     * @param msg local error message
     */
    public MultiCastResourceBuoyException(final String msg) {
        super(msg);
    }

    /**
     * a rethrow...
     * @param msg local error message
     * @param cause the cause
     */
    public MultiCastResourceBuoyException(
            final String msg,
            final Throwable cause) {
        super(msg, cause);
    }
}
