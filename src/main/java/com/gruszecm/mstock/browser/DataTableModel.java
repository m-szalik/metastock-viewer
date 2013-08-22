package com.gruszecm.mstock.browser;

import com.mac.verec.models.Instrument;
import com.mac.verec.models.Quote;

import javax.swing.table.AbstractTableModel;

public class DataTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -977981927441019303L;
	private static final String[] colNames = new String[] {
		"record no", "date", "open", "low", "high", "close", "interest", "volume"
	};
	
	private Instrument instrument;
	
	public DataTableModel(Instrument instrument) {
		this.instrument = instrument;
	}

	public int getColumnCount() {
		return colNames.length;
	}

	public int getRowCount() {
		return instrument.quotes.length;
	}
	
	@Override
	public String getColumnName(int column) {
		return colNames[column];
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		Object obj = get(0, columnIndex);	
		if (obj == null) return super.getColumnClass(columnIndex);
		else return obj.getClass(); 
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		return get(rowIndex, columnIndex);
	}
	
	private Object get(int q, int index) {
		Quote quote = instrument.quotes[q];
		switch (index) {
		case 0: return q;
		case 1: return quote.date;
		case 2: return quote.open;
		case 3: return quote.low;
		case 4: return quote.high;
		case 5: return quote.close;
		case 6: return quote.interest;
		case 7: return quote.volume;
		default:
			return null;
		}
	}
	
}