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

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.apache.commons.lang3.StringEscapeUtils;
import org.bitbucket.mlopatkin.android.logviewer.search.HighlightStrategy;
import org.bitbucket.mlopatkin.android.logviewer.widgets.DecoratingCellRenderer;

public class TextHighlightCellRenderer implements DecoratingCellRenderer {

    private TableCellRenderer inner;
    private static final String highlightBackgroundColor = "yellow";
    private static final String highlightTextColor = "red";
    private static final String spanBegin = String.format(
            "<span style='color: %s; background-color: %s'>", highlightTextColor,
            highlightBackgroundColor);
    private static final String spanEnd = "</span>";
    private static final String placeholderBegin = "=====@@@";
    private static final String placeholderEnd = "@@@=====";

    private HighlightStrategy strategy;

    @Override
    public void setInnerRenderer(TableCellRenderer renderer) {
        inner = renderer;
    }

    private String escapeHighlighted(String value) {
        String escaped = StringEscapeUtils.escapeHtml3(value);
        return escaped.replace(placeholderBegin, spanBegin).replace(placeholderEnd, spanEnd);
    }

    private String highlightMatches(String value) {
        return strategy.highlightOccurences(value);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
        Component c = inner.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                column);
        if ((c instanceof JLabel)) {
            JLabel result = (JLabel) c;
            if (strategy != null) {
                int modelColumn = table.convertColumnIndexToModel(column);
                if (modelColumn == LogRecordTableModel.COLUMN_MSG
                        || modelColumn == LogRecordTableModel.COLUMN_TAG) {
                    String valueString = value.toString();
                    result.setText("<html>" + escapeHighlighted(highlightMatches(valueString))
                            + "</html>");
                }
            }
        } else if (c instanceof TextHighlighter) {
            TextHighlighter th = (TextHighlighter) c;
            if (strategy != null) {
                int modelColumn = table.convertColumnIndexToModel(column);
                if (modelColumn == LogRecordTableModel.COLUMN_MSG
                        || modelColumn == LogRecordTableModel.COLUMN_TAG) {
                    strategy.highlightOccurences(value.toString(), th);
                }
            }
        }
        return c;
    }

    public void setHighlightStrategy(HighlightStrategy strategy) {
        this.strategy = strategy;
        if (strategy != null) {
            strategy.setHighlights(placeholderBegin, placeholderEnd);
        }
    }
}
