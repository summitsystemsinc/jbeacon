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
