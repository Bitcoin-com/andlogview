package name.mlopatkin.andlogview.ui.logtable;

import name.mlopatkin.andlogview.liblogcat.TimeFormatUtils;

import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.Date;

public class BjdCellRenderer extends DefaultTableCellRenderer {
        @Override
        protected void setValue(Object value) {
            super.setValue("bjd" + value);
            super.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
        }
}
