package org.jsoftware.mstock.browser;

import org.jsoftware.mstock.models.Instrument;
import org.jsoftware.mstock.models.NumberDate;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.Date;


public class DataFrame extends AbstractBrowserFrame {
    private static final long serialVersionUID = 463603545282068111L;
    private final Instrument instrument;
    private JTable table;
    private static RowFilter<DataTableModel, Integer> currentFilter = DataFilter.NONE;

    public DataFrame(MasterFrame parent, JDesktopPane desktop, Instrument instrument) {
        super(parent, desktop);
        this.instrument = instrument;
        setFrameIconByName("data.gif");
        init();
    }

    public static void setFilterType(RowFilter<DataTableModel, Integer> filter, JDesktopPane desktopPane) {
        if (! filter.equals(currentFilter)) {
            currentFilter = filter;
            for (int i = 0; i < desktopPane.getComponentCount(); i++) {
                Component cmp = desktopPane.getComponent(i);
                if (cmp instanceof DataFrame) {
                    ((DataFrame) cmp).setFilterTypeUpdate();
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void setFilterTypeUpdate() {
        ((TableRowSorter<DataTableModel>) table.getRowSorter()).setRowFilter(currentFilter);
    }

    private void init() {
        setTitle("Data for " + instrument.name + " (" + instrument.symbol + ")");
        if (instrument.quotes == null || instrument.quotes.length == 0) {
            JPanel panel = new JPanel();
            panel.setSize(300, 100);
            JLabel jlabel = new JLabel("No data found for \"" + instrument.name + "\".");
            panel.add(jlabel);
            panel.setBackground(Color.RED);
            getContentPane().add(panel);
        } else {
            table = new JTable(new DataTableModel(instrument));
            table.setDefaultRenderer(Date.class, new DateRenderer("yyyy-MM-dd EEE", "yyyy-MM-dd HH:mm:ss"));
            table.setDefaultRenderer(NumberDate.class, new DateRenderer("yyyy-MM-dd EEE", "yyyy-MM-dd HH:mm:ss"));
            table.setDefaultRenderer(Float.class, new FloatRenderer());
            table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            table.setAutoCreateRowSorter(true);
            setFilterTypeUpdate();
            JScrollPane pane = new JScrollPane(table);
            getContentPane().add(pane);
        }
        setVisible(true);
        pack();
    }

}
