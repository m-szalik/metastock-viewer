package com.gruszecm.mstock.browser;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JDesktopPane;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.MenuElement;
import javax.swing.ProgressMonitor;
import javax.swing.event.InternalFrameEvent;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.mac.verec.datafeed.metastock.Reader;
import com.mac.verec.models.Instrument;
import com.mac.verec.models.NumberDate;

public class MasterFrame extends AbstractBrowserFrame implements MouseListener, Comparable<MasterFrame>, ActionListener {
	private MasterTabModel model;
	private Reader reader;
	private File file;
	private Browser browser;
	private JTable table;
	
	
	public MasterFrame(Browser browser, File dir, File file, JDesktopPane desktopPane, ProgressMonitor progressMonitor, List<ErrorRecord> errorRecords) throws IOException, InterruptedException {
		super(null, desktopPane);
		this.browser = browser;
		this.file = file;
		setTitle("Index of " + file.getAbsolutePath());
		dir.mkdirs();
		System.out.println("Temporary directory " + dir);
		FileUtils.writeStringToFile(new File(dir, ".info.txt"), "Data from " + file.getAbsolutePath());
		ZipInputStream stream = new ZipInputStream(new FileInputStream(file));
		progressMonitor.setNote("Extracting files...");
		try {
			ZipEntry ze;
			int processc = progressMonitor.getMaximum()/2;
			while ((ze=stream.getNextEntry()) != null) {
				processc ++;
				File f = new File(dir, ze.getName());
				FileOutputStream out = new FileOutputStream(f);
				IOUtils.copy(stream, out);
				progressMonitor.setProgress(processc);
				if (progressMonitor.isCanceled()) {
					throw new InterruptedException();
				}
			}
		} finally {
			IOUtils.closeQuietly(stream);
		}
		Reader reader = new Reader(dir.getAbsolutePath() + "/", true, progressMonitor, errorRecords);
		model = new MasterTabModel(reader);
		table = new JTable(model);
		table.setToolTipText("Double click on instrument row to see data.");
		table.addMouseListener(this);
		table.setDefaultRenderer(Date.class, new DateRenderer("yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss"));
		table.setDefaultRenderer(NumberDate.class, new DateRenderer("yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss"));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setAutoCreateRowSorter(true);
		JScrollPane pane = new JScrollPane(table);
		getContentPane().add(pane);
		setVisible(true);
		setFrameIconByName("index.gif");
		pack();
		this.reader = reader;
	}

	
	@Override
	public void internalFrameActivated(InternalFrameEvent e) {
		super.internalFrameActivated(e);
		browser.chartsMI.setEnabled(true);
		for(Component menuel : browser.chartsMI.getMenuComponents()) {
			if (menuel instanceof JMenuItem) {
				JMenuItem jmi = (JMenuItem) menuel;
				jmi.addActionListener(this);
			}
		}
	}
	
	@Override
	public void internalFrameDeactivated(InternalFrameEvent e) {
		super.internalFrameDeactivated(e);
		browser.chartsMI.setEnabled(false);
		for(MenuElement menuel : browser.chartsMI.getSubElements()) {
			if (menuel instanceof JMenuItem) {
				JMenuItem jmi = (JMenuItem) menuel;
				jmi.removeActionListener(this);
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		int[] selectedColumns = table.getSelectedColumns();
		if (selectedColumns.length != 1) {
			JOptionPane.showMessageDialog(this, "Select single instrument.", "Wrong!", JOptionPane.ERROR_MESSAGE);
		} else {
			Instrument instrument = reader.getInstrument(selectedColumns[0]);
			Chart1Frame chart1frame = new Chart1Frame(this, getDesktopPane(), instrument);
			chart1frame.pack();
			desktop.add(chart1frame);
			chart1frame.setVisible(true);
		}
	}
	
	public Reader getReader() {
		return reader;
	}
	
	public File getFile() {
		return file;
	}
	



	private static final long serialVersionUID = -3281351392506584562L;

	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() > 1 && e.getSource() instanceof JTable) {
			JTable tab = (JTable) e.getSource();
			int index = tab.getSelectedRow();
			Instrument instrument = model.getInstrument(index);
			DataFrame dataFrame = new DataFrame(this, desktop, instrument);
			desktop.add(dataFrame);
			desktop.moveToFront(dataFrame);
		}
	}
	public void mouseEntered(MouseEvent e) {	
	}
	public void mouseExited(MouseEvent e) {	
	}
	public void mousePressed(MouseEvent e) {
	}
	public void mouseReleased(MouseEvent e) {
	}
	public int compareTo(MasterFrame o) {
		return this.file.compareTo(o.file);
	}
}
