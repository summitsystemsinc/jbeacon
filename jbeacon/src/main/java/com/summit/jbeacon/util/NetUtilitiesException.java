/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.summit.jbeacon.util;

import java.net.SocketException;

/**
 *
 * @author justin
 */
public class NetUtilitiesException extends Exception {

    public NetUtilitiesException(String string) {
        super(string);
    }

    NetUtilitiesException(String message, Throwable ex) {
        super(message, ex);
    }

}
