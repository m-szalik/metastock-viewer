package com.gruszecm.mstock.browser;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class FloatRenderer extends DefaultTableCellRenderer {
    private static final long serialVersionUID = 5310683747544174196L;

    public FloatRenderer() {
        setOpaque(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Float f = (Float) value;
        Component rr = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        Color color = null, bgColor = null;
        if (f == null) {
            bgColor = Color.BLACK;
            color = Color.WHITE;
            setText("NULL");
        } else {
            if (f <= 0 && !isSelected) {
                bgColor = Color.PINK;
            }
            setText(Float.toString(f));
        }
        setForeground(Color.BLACK);
        setBackground(Color.WHITE);
        if (bgColor != null) setBackground(bgColor);
        if (color != null) setForeground(color);
        return rr;
    }

}
