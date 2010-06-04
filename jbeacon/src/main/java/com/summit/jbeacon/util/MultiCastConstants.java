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


/**
 * This class contains constants that will be used throughout the project.
 * @author justin smith
 */
public final class MultiCastConstants {
    /**
     * Default Read Timeout.
     */
    public static final int DEFAULT_READ_TIMEOUT = 500;

    /**
     * Default thread wait
     */
    public static final int DEFAULT_THREAD_WAIT = 500;

    /**
     * Does nothing, disables external instantiation.
     */
    private MultiCastConstants() {
    }
    /**
     * Default datagram size to use.
     * <br/>
     * <b>256</b>
     */
    public static final int DEFAULT_DATAGRAM_SIZE = 256;
    /**
     * This is the default notification that the Bouy listens for and the
     * Beacon broadcasts.<br/>
     * Default is <b>jBeacon_hello</b>
     */
    public static final String DEFAULT_LISTEN_TEXT = "jBeacon_hello";
    /**
     * Default port for the multicast group.
     * <br/>
     * <b>1901</b>
     */
    public static final int DEFAULT_PORT = 1901;

    /**
     * Default MultiCast group.
     */
    public static final String DEFAULT_GROUP = "230.0.0.1";

    /**
     * Maximum number of connections at once.
     */
    public static final int DEFAULT_MAX_CONNECTIONS = 50;
    /**
     * Maximum number of times to retry a connection, if max has been reached
     * on the server side.
     */
    public static final int DEFAULT_MAX_RETRIES = 10;

    /**
     * Default listener port.
     */
    public static final int DEFAULT_LISTENER_PORT = 37384;
}
