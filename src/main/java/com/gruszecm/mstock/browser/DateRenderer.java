package com.gruszecm.mstock.browser;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.table.DefaultTableCellRenderer;

import com.mac.verec.models.NumberDate;

public class DateRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -1976804846865659974L;
	private SimpleDateFormat sdf1, sdf2;
	
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
			String text;
			Date d = (Date) value;
			text = sdf1.format(d);
			setText(text);
			text = sdf2.format(d);
			if (value instanceof NumberDate) {
				NumberDate nd = (NumberDate) value;
				text = text + " (" + nd.getNumberValue() + ")";
				if (nd.getBytes() != null) {
					text += "[";
					for(byte b : nd.getBytes()) text += b + " ";
					text = text.trim() + "]";
				}
			}
			setToolTipText(text);
		} else {
			throw new RuntimeException();
		}
	}

}
