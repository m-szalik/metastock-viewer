package com.gruszecm.mstock.browser;

import com.gruszecm.mstock.tests.*;
import com.mac.verec.datafeed.metastock.Reader;
import com.mac.verec.models.Instrument;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.StringWriter;
import java.util.Enumeration;

public class TestFrame extends AbstractBrowserFrame {
	private static final long serialVersionUID = 2136500585019304598L;
	private Reader reader;
	private JTable table;
	private JMenuItem copyTestRaportToClipboard;

	private AbstractMstockTest[] tests = new AbstractMstockTest[] { new DateFirstTest(false), new DateLastTest(false), new DateFirstTest(true), new DateLastTest(true), new QuotValueTest(), new QuotMinMaxTest(), new DatNoOfRecordsTest() };

	public TestFrame(JInternalFrame parent, JDesktopPane desktopPane, Reader reader) {
		super(parent, desktopPane);
		setTitle("Test results");
		this.reader = reader;
		String[] colNames = new String[tests.length + 1];
		colNames[0] = "instrument";
		for (int i = 1; i < colNames.length; i++)
			colNames[i] = tests[i - 1].getName();
		Object[][] data = new Object[reader.getInstrumentCount()][colNames.length];
		int y = 0;
		for (Enumeration<Instrument> en = reader.getInstruments(); en.hasMoreElements(); y++) {
			Instrument instrument = en.nextElement();
			data[y][0] = instrument.name;
		}
		table = new JTable(data, colNames);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setAutoCreateRowSorter(true);
		table.setDefaultRenderer(Object.class, new TestResultRenderer());
		JScrollPane pane = new JScrollPane(table);
		getContentPane().add(pane);
		setVisible(true);
		pack();
		this.reader = reader;

		JMenuBar menuBar = new JMenuBar();
		JMenu menuFile = new JMenu("File");
		menuFile.setMnemonic(KeyEvent.VK_F);
		copyTestRaportToClipboard = new JMenuItem("Copy test raport to clipboard");
		copyTestRaportToClipboard.setMnemonic(KeyEvent.VK_C);
		copyTestRaportToClipboard.setEnabled(false);
		menuFile.add(copyTestRaportToClipboard);
		copyTestRaportToClipboard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StringWriter output = new StringWriter();
				testRaport(output);
				Transferable contents = new StringSelection(output.getBuffer().toString());
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(contents, new ClipboardOwner() {
					public void lostOwnership(Clipboard clipboard, Transferable contents) {
					}
				});
			}
		});
		menuBar.add(menuFile);
		setJMenuBar(menuBar);
		desktopPane.add(this);
		setVisible(true);
	}
	
	public void start() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				super.run();
				doTests();
			}
		};
		thread.start();
	}

	private void testRaport(StringWriter output) {
		output.append("Test of ...").append('\n').append('\n');
		for (int y = 0; y < table.getRowCount(); y++) {
			for (int x = 0; x < table.getColumnCount(); x++) {
				Object value = table.getValueAt(y, x);
				output.append(table.getColumnName(x));
				output.append(": ");
				output.append(value.toString());
				if (value instanceof TestResult) {
					TestResult tr = (TestResult) value;
					if (!tr.isImportant())
						output.append(" (you can ignore this test result)");
				}
				output.append('\n');
			}
			output.append('\n');
		}
		output.flush();
	}


	public void doTests() {
		int y = 0;
		Enumeration<Instrument> en = reader.getInstruments();
		for (; en.hasMoreElements(); y++) {
			Instrument instrument = en.nextElement();
			for (int x = 0; x < tests.length; x++) {
				AbstractMstockTest test = tests[x];
				TestResult tr = test.test(instrument);
				table.setValueAt(tr, y, x + 1);
			}
		}
		copyTestRaportToClipboard.setEnabled(true);
	}

}
