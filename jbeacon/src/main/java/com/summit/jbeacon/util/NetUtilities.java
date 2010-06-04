/*
 * Copyright 2009 Summit Management Systems, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.summit.jbeacon.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

/**
 *
 * @author justin
 */
public class NetUtilities {

    public static final List<String> KNOWN_LOCAL_HOSTS =
	    Arrays.asList(new String[]{
		"127.0.0.1",
		"127.0.1.1",
		"0.0.0.0"
	    });

    private NetUtilities() {
    }

    public static final InetAddress guessLocalInetAddress() throws NetUtilitiesException {
	try {
	    Enumeration<NetworkInterface> nics =
		    NetworkInterface.getNetworkInterfaces();
	    InetAddress addr = null;
	    while (nics.hasMoreElements()) {
		NetworkInterface current = nics.nextElement();
		if (current.getHardwareAddress() == null) {
		    continue;
		} else {
		    Enumeration<InetAddress> addresses =
			    current.getInetAddresses();
		    while (addresses.hasMoreElements()) {
			InetAddress currentAddr =
				addresses.nextElement();
			if (currentAddr.getClass()
				!= Inet4Address.class) {
			    continue;
			}
			if (KNOWN_LOCAL_HOSTS.contains(
				currentAddr.toString())) {
			    continue;
			} else {
			    addr = currentAddr;
			    break;
			}
		    }
		    if (addr != null) {
			break;
		    }
		}
	    }
	    if (addr != null) {
		return addr;
	    } else {
		throw new NetUtilitiesException("Unable to "
			+ "guess external address...");
	    }
	} catch (SocketException ex) {
	    throw new NetUtilitiesException(
		    ex.getMessage(), ex);
	}
    }
}
