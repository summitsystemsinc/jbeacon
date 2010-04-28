/*
 * Copyright 2010 Summit Management Systems, Inc.
 * All rights reserved.
 *
 * MODIFICATION/REDISTRIBUTION OF ANY PART OF THIS SOFTWARE (WHICH
 * INCLUDES, BUT IS NOT LIMITED TO, SOURCE CODE AND/OR COMPILED
 * BINARIES) IS STRICTLY PROHIBITED, WITH THE FOLLOWING EXCEPTION:
 *
 *     API documentation may be freely redistributed, without
 *     modification, provided that no fees are charged.
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
		for (int i=1;i<=this.maxRetries;i++) {
			try {
				results = operationToRetry();
				if (i>1 && logger.isWarnEnabled()) {
					logger.warn(MessageFormat.format("Operation succeeded on the {0, number,integer}{0, choice, 1#st | 2#nd | 3#rd | 3<th} attempt.", i));
				}
				break;
			}catch(Exception ex) {
				if (i<this.maxRetries) {
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
