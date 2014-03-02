/*
 * Copyright 2011 Mikhail Lopatkin
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
package org.bitbucket.mlopatkin.android.logviewer;

import javax.swing.RowFilter;

import com.google.common.base.Predicate;

import org.bitbucket.mlopatkin.android.liblogcat.LogRecord;
import org.bitbucket.mlopatkin.android.liblogcat.filters.LogRecordFilter;

/**
 * Wrapper over {@link LogRecordFilter} to match {@link RowFilter} interface.
 */
public class LogRecordFilterWrapper extends RowFilter<LogRecordTableModel, Integer> {

    private Predicate<LogRecord> filter;

    public LogRecordFilterWrapper(Predicate<LogRecord> filter) {
        this.filter = filter;
    }

    @Override
    public boolean include(Entry<? extends LogRecordTableModel, ? extends Integer> entry) {
        LogRecord record = entry.getModel().getRowData(entry.getIdentifier());
        return filter.apply(record);
    }
}
