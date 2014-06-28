package org.jsoftware.mstock.browser;

import org.jsoftware.mstock.metastock.Reader;
import org.jsoftware.mstock.models.Instrument;

import javax.swing.table.AbstractTableModel;

public class MasterTabModel extends AbstractTableModel {
    private static final long serialVersionUID = 6467417063330289337L;
    private final Reader reader;
    private static final String[] colNames = new String[]{
            "name", "symbol", "record count (index)", "first date", "last date", "filename",
            "quotas", "valid quotas", "record len"
    };

    public MasterTabModel(Reader reader) {
        this.reader = reader;
    }


    @Override
    public String getColumnName(int column) {
        return colNames[column];
    }

    public int getColumnCount() {
        return colNames.length;
    }

    public int getRowCount() {
        return reader.getInstrumentCount();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return get(rowIndex, columnIndex);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        Object o = get(0, columnIndex);
        if (o == null) return Object.class;
        return o.getClass();
    }

    private Object get(int index, int col) {
        Instrument instrument = reader.getInstrument(index);
        Object ret;
        switch (col) {
            case 0:
                ret = instrument.name;
                break;
            case 1:
                ret = instrument.symbol;
                break;
            case 2:
                ret = instrument.getMasterFileRecord().getRecordCount();
                break;
            case 3:
                ret = instrument.getMasterFileRecord().getFirstDate();
                break;
            case 4:
                ret = instrument.getMasterFileRecord().getLastDate();
                break;
            case 5:
                ret = instrument.getMasterFileRecord().getFileName();
                break;
            case 6:
                ret = instrument.getQuotes().length;
                break;
            case 7:
                ret = instrument.getValidQuotas();
                break;
            case 8:
                ret = instrument.getMasterFileRecord().getRecordLength();
                break;
            default:
                ret = null;
                break;
        }
        return ret;
    }

    public Instrument getInstrument(int index) {
        return reader.getInstrument(index);
    }

}
