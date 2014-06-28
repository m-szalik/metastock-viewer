package org.jsoftware.mstock.browser;

import org.jsoftware.mstock.tests.TestResult;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class TestResultRenderer extends DefaultTableCellRenderer {
    private static final long serialVersionUID = 5310683747544174196L;
    private static final Color OK_BACKGROUND = new Color(217, 255, 200);
    private static final Color LIGHT_PINK = new Color(254, 224, 224);

    public TestResultRenderer() {
        setOpaque(true);
        ToolTipManager.sharedInstance().setDismissDelay(1000 * 120);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setBackground(Color.WHITE);
        setForeground(Color.BLACK);
        Component rr = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (value instanceof TestResult) {
            TestResult tr = (TestResult) value;
            setAlignmentX(0.5f);
            if (tr.isOk()) {
                if (tr.getInfoMessages().size() > 0) {
                    setBackground(Color.BLUE);
                    setText(TestResult.toString(tr.getInfoMessages()));
                } else {
                    setForeground(Color.GREEN);
                    setBackground(OK_BACKGROUND);
                    setText("ok");
                }
            } else {
                if (tr.isImportant()) {
                    setBackground(Color.PINK);
                    setForeground(Color.RED);
                } else {
                    setBackground(LIGHT_PINK);
                    setForeground(Color.RED);
                }
                setText(TestResult.toString(tr.getErrorMessages()));
                setToolTipText(TestResult.toString(tr.getErrorMessages()));
//				for(String s : tr.getErrorMessages()) {
//					// 	TODO				
//				}
            }

        }
        return rr;
    }

//	@Override
//	public JToolTip createToolTip() {
//		return multilineToolTip;
//	}
}
