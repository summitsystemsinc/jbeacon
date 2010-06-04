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

import java.text.MessageFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author <a href="mailto:steven.swor@summitsystemsinc.com" title="steven.swor@summitsystemsinc.com">Steven Swor</a>
 */
public abstract class AutomaticallyRetryingOperation<T extends Object> {

    private final int maxRetries;
    private static final Log logger = LogFactory.getLog(AutomaticallyRetryingOperation.class);

    public AutomaticallyRetryingOperation(final int maxAttempts) {
	super();
	this.maxRetries = maxAttempts;
    }

    protected abstract T operationToRetry() throws Exception;

    public T execute() throws Exception {
	T results = null;
	for (int i = 1; i <= this.maxRetries; i++) {
	    try {
		results = operationToRetry();
		if (i > 1 && logger.isWarnEnabled()) {
		    logger.warn(MessageFormat.format("Operation succeeded on the {0, number,integer}{0, choice, 1#st | 2#nd | 3#rd | 3<th} attempt.", i));
		}
		break;
	    } catch (Exception ex) {
		if (i < this.maxRetries) {
		    if (logger.isWarnEnabled()) {
			logger.warn(MessageFormat.format("Operation failed for the {0, number,integer}{0, choice, 1#st | 2#nd | 3#rd | 3<th} time.  Retrying.", i));
		    }
		} else {
		    throw ex;
		}
	    }
	}
	return results;
    }
}
