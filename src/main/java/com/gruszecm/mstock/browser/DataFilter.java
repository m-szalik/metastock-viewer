package com.gruszecm.mstock.browser;

import javax.swing.*;

public abstract class DataFilter extends RowFilter<DataTableModel, Integer> {

    public static final RowFilter<DataTableModel, Integer> NONE = new RowFilter<DataTableModel, Integer>() {
        @Override
        public boolean include(javax.swing.RowFilter.Entry<? extends DataTableModel, ? extends Integer> entry) {
            return true;
        }
    };

    public static final RowFilter<DataTableModel, Integer> ALL_INVALID = new RowFilter<DataTableModel, Integer>() {
        @Override
        public boolean include(javax.swing.RowFilter.Entry<? extends DataTableModel, ? extends Integer> entry) {
            int c = countInvalid(entry);
            return c < 4;
        }
    };

    public static final RowFilter<DataTableModel, Integer> ONE_INVALID = new RowFilter<DataTableModel, Integer>() {
        @Override
        public boolean include(javax.swing.RowFilter.Entry<? extends DataTableModel, ? extends Integer> entry) {
            return countInvalid(entry) == 0;
        }
    };


    private static int countInvalid(Entry<? extends DataTableModel, ? extends Integer> entry) {
        int c = 0;
        Object value;
        for (int i = 0; i < entry.getValueCount(); i++) {
            value = entry.getValue(i);
            if (value instanceof Number) {
                if (((Number) value).longValue() < 0) c++;
            }
        }
        return c;
    }
}
