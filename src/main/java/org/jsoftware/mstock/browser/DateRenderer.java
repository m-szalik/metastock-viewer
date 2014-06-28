package org.jsoftware.mstock.browser;

import org.jsoftware.mstock.models.NumberDate;

import javax.swing.table.DefaultTableCellRenderer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = -1976804846865659974L;
    private final SimpleDateFormat sdf1;
    private final SimpleDateFormat sdf2;

    public DateRenderer(String shortFormat, String longFormat) {
        sdf1 = new SimpleDateFormat(shortFormat);
        sdf2 = new SimpleDateFormat(longFormat);
    }


    @Override
    protected void setValue(Object value) {
        if (value == null) {
            setText("???");
            return;
        }
        if (value instanceof Date) {
            Date d = (Date) value;
            setText(sdf1.toString());
            StringBuilder text = new StringBuilder(sdf2.format(d));
            if (value instanceof NumberDate) {
                NumberDate nd = (NumberDate) value;
                text.append(" (").append(nd.getNumberValue()).append(")");
                if (nd.getBytes() != null) {
                    text.append("[");
                    for (byte b : nd.getBytes()) {
                        text.append(b).append(' ');
                    }
                    text.trimToSize();
                    text.append("]");
                }
            }
            setToolTipText(text.toString());
        } else {
            throw new RuntimeException();
        }
    }

}
