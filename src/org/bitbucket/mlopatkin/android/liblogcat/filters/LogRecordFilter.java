/*
 * Copyright 2013 Mikhail Lopatkin
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

package org.bitbucket.mlopatkin.android.liblogcat.filters;

import com.google.common.base.Predicate;

import org.bitbucket.mlopatkin.android.liblogcat.LogRecord;

/**
 * Generic filter interface.
 */
public interface LogRecordFilter extends Predicate<LogRecord> {

    /**
     * Checks if the record matches the filter.
     *
     * @param record the record to match
     * @return {@code true} if the records matched the filter
     */
    boolean apply(LogRecord record);

    /**
     * Combines existing filter and the other filter using "AND" logical operation. The resulting
     * filter will include a record only if it is included by this and other filter.
     *
     * @param other the filter to append
     * @return the new filter that combines this and other
     */
    LogRecordFilter and(LogRecordFilter other);
}
