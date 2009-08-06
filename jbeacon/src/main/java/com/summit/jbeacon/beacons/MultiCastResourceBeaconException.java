/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.summit.jbeacon.beacons;

/**
 *
 * @author justin
 */
public class MultiCastResourceBeaconException extends Exception {
    /**
     * Initial exception.
     * @param msg local message
     */
    public MultiCastResourceBeaconException(final String msg) {
        super(msg);
    }

    /**
     * Retrow exception.
     * @param msg local message
     * @param cause rethrown cause.
     */
    public MultiCastResourceBeaconException(final String msg,final Throwable cause) {
        super(msg, cause);
    }
}
